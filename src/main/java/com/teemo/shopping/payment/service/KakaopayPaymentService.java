package com.teemo.shopping.payment.service;

import com.teemo.shopping.external_api.kakao.dto.KakaopayCancelParameter;
import com.teemo.shopping.external_api.kakao.dto.KakaopayReadyParameter;
import com.teemo.shopping.external_api.kakao.service.KakaopayService;
import com.teemo.shopping.payment.domain.KakaopayPayment;
import com.teemo.shopping.payment.domain.Payment;
import com.teemo.shopping.payment.dto.KakaopayPaymentDTO;
import com.teemo.shopping.payment.domain.enums.KakaopayAPIStates;
import com.teemo.shopping.payment.domain.enums.PaymentStates;
import com.teemo.shopping.payment.repository.PaymentRepository;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.payment.service.kakaopay.KakaopayPaymentErrorService;
import com.teemo.shopping.payment.service.kakaopay.KakaopayPaymentRefundService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KakaopayPaymentService extends PaymentService {

    @Autowired
    private PaymentRepository<KakaopayPayment> kakaopayPaymentRepository;
    @Autowired
    private KakaopayService kakaopayService;
    @Autowired
    private KakaopayPaymentRefundService kakaopayPaymentRefundService;
    @Value("${kakao.cid}")
    private String cid;
    @Value("${kakao.partner-user-id}")
    private String partnerUserId;
    @Value("${server.origin}")
    private String origin;
    @Autowired
    private KakaopayPaymentErrorService kakaopayPaymentErrorService;

    @Override
    @Transactional
    public void refund(Long paymentId, int amount) {
        KakaopayPayment payment = kakaopayPaymentRepository.findById(paymentId)
            .get();
        if (!(payment.getState().equals(PaymentStates.PARTIAL_REFUNDED)
            || payment.getState().equals(PaymentStates.SUCCESS))) { // 환불 가능 조건
            throw new IllegalStateException("환불 가능한 상태 아님.");
        }
        if (payment.getRefundableAmount() < amount) {    // 환불할 금액이 환불가능 금액보다 크면 안됌
            throw new IllegalStateException("환불 가능한 금액이 아님.");
        }

        payment.setState(PaymentStates.PENDING_REFUND);

        kakaopayService.cancelKakaopay(
            KakaopayCancelParameter.builder()
                .amount(amount).cid(payment.getCid()).tid(payment.getTid()).build())
            .doOnError((error) -> {
                kakaopayPaymentErrorService.onRefundError(paymentId);
            })
            .doOnSuccess((response) -> {
                kakaopayPaymentRefundService.onRefund(paymentId, response);
            })
            .subscribe();

    }

    @Override
    @Transactional
    public void pay(Long paymentId) {
        KakaopayPayment payment = kakaopayPaymentRepository.findById(paymentId).get();
        if (!payment.getState().equals(PaymentStates.PENDING)) {
            throw new IllegalStateException("결제 가능한 상태가 아님.");
        }
        String partnerOrderId = payment.getId().toString();
        String redirectSecret = UUID.randomUUID().toString();
        String approvalURL = origin + "/kakao/success";
        String failURL = origin + "/kakao/fail";
        String cancelURL = origin + "/kakao/cancel";
        Integer amount = payment.getAmount();
        Integer quantity = 1;
        try {
            Map<String, String> additionalQuery = new HashMap<>();
            additionalQuery.put("next_redirect", payment.getNextRedirect());     //Todo : URL Builder로 대체
            additionalQuery.put("payment_id", paymentId.toString());
            additionalQuery.put("redirect_secret", redirectSecret); //Redirect 검증용
            KakaopayReadyParameter request = KakaopayReadyParameter.builder().itemName(payment.getItemName())
                .partnerOrderId(partnerOrderId)
                .partnerUserId(partnerUserId)
                .cid(cid)
                .approvalURL(approvalURL)
                .failURL(failURL)
                .cancelURL(cancelURL)
                .amount(amount)
                .quantity(quantity)
                .additionalQuery(additionalQuery)
                .build();
            payment.updatePartnerOrderId(partnerOrderId);
            payment.updatePartnerUserId(partnerUserId);
            payment.updateCid(cid);
            payment.updateRedirectSecret(redirectSecret);
            payment.updateKakaopayAPIState(KakaopayAPIStates.READY_PENDING);
            kakaopayService.readyKakaopay(request)
                .doOnError((error) -> {
                    kakaopayPaymentErrorService.onPayError(paymentId);
                })
                .doOnSuccess((response) -> {
                    payment.updateKakaopayAPIState(KakaopayAPIStates.REDIRECT_PENDING);
                    payment.updateTid(response.getTid());
                    payment.updateRedirect(response.getNextRedirectPcUrl());
                })
                .block();
        } catch (Exception e) {
            kakaopayPaymentErrorService.onPayError(paymentId);
            throw new IllegalStateException("카카오 API 에러");
        }
    }


    public KakaopayPaymentDTO get(Long paymentId) {
        KakaopayPayment kakaopayPayment = kakaopayPaymentRepository.findById(paymentId).get();
        return KakaopayPaymentDTO.from(kakaopayPayment);
    }

    @Override
    public Class<? extends Payment> getTarget() {
        return KakaopayPayment.class;
    }
}
