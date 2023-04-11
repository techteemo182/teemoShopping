package com.teemo.shopping.order.service;

import com.teemo.shopping.order.domain.KakaopayPayment;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.enums.PaymentStatus;
import com.teemo.shopping.order.dto.KakaopayRedirectParameter;
import com.teemo.shopping.order.dto.KakaopayRedirectParameter.KakaopayRedirectType;
import com.teemo.shopping.order.dto.PaymentStatusUpdateObserverContext;
import com.teemo.shopping.order.dto.payment_create_param.KakaopayPaymentCreateParam;
import com.teemo.shopping.order.repository.KakaopayPaymentRepository;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.external_api.kakao.KakaopayService;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveResponse;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIReadyResponse;
import com.teemo.shopping.external_api.kakao.dto.KakaopayApproveParameter;
import com.teemo.shopping.external_api.kakao.dto.KakaopayCancelParameter;
import com.teemo.shopping.external_api.kakao.dto.KakaopayReadyParameter;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KakaopayPaymentService extends
    AllProductPaymentService<KakaopayPaymentCreateParam> {    //전략 패턴

    @Autowired
    private KakaopayPaymentRepository kakaopayPaymentRepository;
    @Autowired
    private KakaopayService kakaopayService;
    @Autowired
    private OrderRepository orderRepository;


    @Override
    @Transactional(rollbackOn = RuntimeException.class)
    public Optional<Long> create(KakaopayPaymentCreateParam param) {
        int amount = param.getAmount();
        Order order = orderRepository.findById(param.getOrderId()).get();
        KakaopayPayment kakaopayPayment = KakaopayPayment.builder().status(PaymentStatus.PENDING)
            .amount(amount).order(order).build();
        kakaopayPayment = kakaopayPaymentRepository.save(kakaopayPayment);

        String itemName = param.getItemName();
        KakaopayAPIReadyResponse response;
        try {
            KakaopayReadyParameter request = KakaopayReadyParameter.builder().itemName(itemName)
                .partnerOrderId(kakaopayPayment.getId().toString()).amount(amount).build();
            response = kakaopayService.readyKakaopay(request)   // throws RuntimeException
                .block();
        } catch (Exception e) {
            throw new IllegalStateException("카카오 API 에러");
        }
        kakaopayPayment.updateRedirect(response.getNextRedirectAppUrl(),
            response.getNextRedirectMobileUrl(), response.getNextRedirectPcUrl()
            , response.getAndroidAppScheme(), response.getIosAppScheme());
        kakaopayPayment.updateTid(response.getTid());
        return Optional.of(kakaopayPayment.getId());
    }

    @Override
    void refund(Long paymentId, int refundAmount) {
        KakaopayPayment payment = kakaopayPaymentRepository.findById(paymentId)
            .get();
        if (!(payment.getStatus() == PaymentStatus.PARTIAL_REFUNDED
            || payment.getStatus() == PaymentStatus.SUCCESS)) { // 환불 가능 조건
            throw new IllegalStateException("환불 불가능한 상태임");
        }
        if (payment.getRefundableAmount() < refundAmount) {    // 환불할 금액이 환불가능 금액보다 크면 안됌
            throw new IllegalStateException("환불할 금액이 환불 가능한 금액보다 큼");
        }

        kakaopayService.cancelKakaopay(
            KakaopayCancelParameter.builder()    //실패시 throw RuntimeException
                .amount(refundAmount).tid(payment.getTid()).build()).block();

        payment.updateRefundedPoint(payment.getRefundedAmount() + refundAmount);
        payment.updateStatus(
            payment.getRefundedAmount() == payment.getAmount() ? PaymentStatus.REFUNDED
                : PaymentStatus.PARTIAL_REFUNDED);
    }

    @Transactional(rollbackOn = RuntimeException.class)
    public Optional<KakaopayAPIApproveResponse> onKakaopayRedirectResponse(
        KakaopayRedirectParameter parameter)
        throws RuntimeException {
        KakaopayRedirectType type = parameter.getType();

        String partnerUserId = parameter.getPartnerUserId();
        String partnerOrderId = parameter.getPartnerOrderId();
        KakaopayPayment kakaopayPayment;
        try {
            kakaopayPayment = kakaopayPaymentRepository.findById(Long.parseLong(partnerOrderId))
                .get();  // throw NoSuchElementException
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

    @Override
    public Class<KakaopayPayment> getTargetPaymentClass() {
        return KakaopayPayment.class;
    }

}

