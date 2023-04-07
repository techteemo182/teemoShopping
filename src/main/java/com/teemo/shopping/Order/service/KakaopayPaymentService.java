package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.KakaopayPayment;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.KakaopayRedirectParameter;
import com.teemo.shopping.Order.dto.KakaopayRedirectParameter.KakaopayRedirectType;
import com.teemo.shopping.Order.dto.PaymentCreateContext;
import com.teemo.shopping.Order.dto.PaymentRefundParameter;
import com.teemo.shopping.Order.dto.PaymentStatusUpdateObserverContext;
import com.teemo.shopping.Order.repository.KakaopayPaymentRepository;
import com.teemo.shopping.external_api.kakao.KakaopayService;
import com.teemo.shopping.external_api.kakao.dto.KakaopayApproveParameter;
import com.teemo.shopping.external_api.kakao.dto.KakaopayCancelParameter;
import com.teemo.shopping.external_api.kakao.dto.KakaopayReadyParameter;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Qualifier("forAllProduct")
@Order(200)
public class KakaopayPaymentService extends PaymentService {    //전략 패턴

    @Autowired
    private KakaopayPaymentRepository kakaopayPaymentRepository;
    @Autowired
    private KakaopayService kakaopayService;


    @Override
    @Transactional(rollbackOn = RuntimeException.class)
    public Optional<Payment> create(PaymentCreateContext context) {
        int remainPrice = context.getRemainPrice();
        com.teemo.shopping.Order.domain.Order order = context.getOrder();
        KakaopayPayment kakaopayPayment = KakaopayPayment.builder().status(PaymentStatus.PENDING)
            .price(remainPrice).order(order).build();
        kakaopayPayment = kakaopayPaymentRepository.saveAndFlush(kakaopayPayment);

        String itemName = String.join(",", context.getGames().stream().map(game -> game.getName())
            .toList());    //GameName 추출 후 "," 으로 Join
        KakaopayReadyParameter request = KakaopayReadyParameter.builder().itemName(itemName)
            .partnerOrderId(kakaopayPayment.getId().toString()).price(remainPrice).build();
        var response = kakaopayService.readyKakaopay(request)   // throws RuntimeException
            .block();
        kakaopayPayment.updateTid(response.getTid());
        kakaopayPaymentRepository.save(kakaopayPayment);

        var createOrderReturnBuilder = context.getCreateOrderReturnBuilder();
        createOrderReturnBuilder.nextRedirectAppUrl(response.getNextRedirectAppUrl());
        createOrderReturnBuilder.nextRedirectMobileUrl(response.getNextRedirectMobileUrl());
        createOrderReturnBuilder.nextRedirectPcUrl(response.getNextRedirectPcUrl());
        context.setRemainPrice(0);
        return Optional.of(kakaopayPayment);
    }

    @Override
    void refund(PaymentRefundParameter parameter) {
        KakaopayPayment payment = kakaopayPaymentRepository.findById(parameter.getPaymentId())
            .get();
        if (!(payment.getStatus() == PaymentStatus.PARTIAL_REFUNDED
            || payment.getStatus() == PaymentStatus.SUCCESS)) { // 환불 가능 조건
            throw new RuntimeException();
        }
        if (payment.getPrice() - payment.getRefundedPrice()
            < parameter.getPrice()) {    // 환불할 금액이 환불가능 금액보다 크면 안됌
            throw new RuntimeException();
        }

        kakaopayService.cancelKakaopay(KakaopayCancelParameter.builder()    //실패시 throw RuntimeException
            .price(parameter.getPrice()).tid(payment.getTid()).build()).block();

        payment.updateRefundedPoint(payment.getRefundedPrice() + parameter.getPrice());
        payment.updateStatus(
            payment.getRefundedPrice() == payment.getPrice() ? PaymentStatus.REFUNDED
                : PaymentStatus.PARTIAL_REFUNDED);
    }

    @Transactional(rollbackOn = RuntimeException.class)
    public void onKakaopayRedirectResponse(KakaopayRedirectParameter parameter)
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
        if (parameter.getType() == KakaopayRedirectType.FAIL
            || parameter.getType() == KakaopayRedirectType.CANCEL) {
            kakaopayPayment.updateStatus(PaymentStatus.CANCEL);
        } else if (parameter.getType() == KakaopayRedirectType.SUCCESS) {
            try {
                var response = kakaopayService.approveKakaopay(
                    KakaopayApproveParameter.builder().pgToken(parameter.getPgToken())
                        .partnerOrderId(kakaopayPayment.getId().toString())
                        .tid(kakaopayPayment.getTid()).build()).block();
                kakaopayPayment.updateStatus(PaymentStatus.SUCCESS);
            } catch (Exception e) {
                kakaopayPayment.updateStatus(PaymentStatus.CANCEL);
            }
        }
        notifyObservers(
            PaymentStatusUpdateObserverContext.builder().payment(kakaopayPayment).build());
    }
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.KAKAOPAY;
    }

}

