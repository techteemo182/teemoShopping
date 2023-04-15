package com.teemo.shopping.order.service;

import com.teemo.shopping.external_api.kakao.service.KakaopayService;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveResponse;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIReadyResponse;
import com.teemo.shopping.external_api.kakao.dto.KakaopayApproveParameter;
import com.teemo.shopping.external_api.kakao.dto.KakaopayCancelParameter;
import com.teemo.shopping.external_api.kakao.dto.KakaopayReadyParameter;
import com.teemo.shopping.order.domain.KakaopayPayment;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.dto.payment.KakaopayPaymentDTO;
import com.teemo.shopping.order.enums.PaymentStatus;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.context.PaymentStatusUpdateObserverContext;
import com.teemo.shopping.order.service.parameter.KakaopayPaymentCreateParameter;
import com.teemo.shopping.order.service.parameter.KakaopayRedirectParameter;
import com.teemo.shopping.order.service.parameter.KakaopayRedirectParameter.KakaopayRedirectType;
import com.teemo.shopping.order.service.parameter.PaymentRefundParameter;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KakaopayPaymentService extends
    PaymentService<KakaopayPaymentCreateParameter> {    //전략 패턴

    @Autowired
    private PaymentRepository<KakaopayPayment> kakaopayPaymentRepository;
    @Autowired
    private KakaopayService kakaopayService;
    @Autowired
    private OrderRepository orderRepository;


    @Override
    @Transactional
    public Optional<Payment> create(KakaopayPaymentCreateParameter parameter) {
        int amount = parameter.getAmount();
        Order order = parameter.getOrder();
        KakaopayPayment kakaopayPayment = KakaopayPayment.builder().status(PaymentStatus.PENDING)
            .amount(amount).order(order).build();
        kakaopayPayment = kakaopayPaymentRepository.save(kakaopayPayment);

        String itemName = parameter.getItemName();
        KakaopayAPIReadyResponse response;
        try {
            KakaopayReadyParameter request = KakaopayReadyParameter.builder().itemName(itemName)
                .partnerOrderId(kakaopayPayment.getId().toString()).amount(amount).redirect(parameter.getRedirect()).build();
            response = kakaopayService.readyKakaopay(request)   // throws RuntimeException
                .block();
        } catch (Exception e) {
            throw new IllegalStateException("카카오 API 에러");
        }
        kakaopayPayment.updateRedirect(response.getNextRedirectAppUrl(),
            response.getNextRedirectMobileUrl(), response.getNextRedirectPcUrl()
            , response.getAndroidAppScheme(), response.getIosAppScheme());
        kakaopayPayment.updateTid(response.getTid());
        return Optional.of(kakaopayPayment);
    }

    @Override
    public void refund(PaymentRefundParameter parameter) {
        KakaopayPayment payment = kakaopayPaymentRepository.findById(parameter.getPaymentId())
            .get();
        if (!(payment.getStatus() == PaymentStatus.PARTIAL_REFUNDED
            || payment.getStatus() == PaymentStatus.SUCCESS)) { // 환불 가능 조건
            throw new RuntimeException();
        }
        if (payment.getRefundableAmount()
            < parameter.getRefundPrice()) {    // 환불할 금액이 환불가능 금액보다 크면 안됌
            throw new RuntimeException();
        }

        kakaopayService.cancelKakaopay(
            KakaopayCancelParameter.builder()    //실패시 throw RuntimeException
                .amount(parameter.getRefundPrice()).tid(payment.getTid()).build()).block();

        payment.updateRefundedPoint(payment.getRefundedAmount() + parameter.getRefundPrice());
        payment.updateStatus(
            payment.getRefundedAmount() == payment.getAmount() ? PaymentStatus.REFUNDED
                : PaymentStatus.PARTIAL_REFUNDED);
    }

    @Transactional
    public Optional<KakaopayAPIApproveResponse> onKakaopayRedirectResponse(
        KakaopayRedirectParameter parameter)
        throws RuntimeException {
        KakaopayRedirectType type = parameter.getType();

        String partnerUserId = parameter.getPartnerUserId();
        String partnerOrderId = parameter.getPartnerOrderId();
        KakaopayPayment kakaopayPayment;
        try {
            kakaopayPayment = kakaopayPaymentRepository.findById(Long.parseLong(partnerOrderId))
                .get();
        } catch (Exception e) {
            throw new RuntimeException("잘못된 접근");
        }
        if (kakaopayPayment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("중복 접근");
        }
        Optional<KakaopayAPIApproveResponse> response = Optional.empty();
        if (parameter.getType() == KakaopayRedirectType.FAIL
            || parameter.getType() == KakaopayRedirectType.CANCEL) {
            kakaopayPayment.updateStatus(PaymentStatus.CANCEL);
        } else if (parameter.getType() == KakaopayRedirectType.SUCCESS) {
            try {
                response = Optional.of(kakaopayService.approveKakaopay(
                    KakaopayApproveParameter.builder().pgToken(parameter.getPgToken())
                        .partnerOrderId(kakaopayPayment.getId().toString())
                        .tid(kakaopayPayment.getTid()).build()).block());
                kakaopayPayment.updateStatus(PaymentStatus.SUCCESS);
            } catch (Exception e) {
                kakaopayPayment.updateStatus(PaymentStatus.CANCEL);
            }
        }
        notifyObservers(
            PaymentStatusUpdateObserverContext.builder().payment(kakaopayPayment).build());
        return response;
    }

    public KakaopayPaymentDTO get(Long paymentId) {
        KakaopayPayment kakaopayPayment = kakaopayPaymentRepository.findById(paymentId).get();
        return KakaopayPaymentDTO.from(kakaopayPayment);
    }
    @Override
    public Class<KakaopayPayment> getPaymentClass() {
        return KakaopayPayment.class;
    }
}
