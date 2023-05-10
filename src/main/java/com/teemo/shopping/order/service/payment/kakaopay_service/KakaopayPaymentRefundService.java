package com.teemo.shopping.order.service.payment.kakaopay_service;

import com.teemo.shopping.external_api.kakao.dto.KakaopayAPICancelResponse;
import com.teemo.shopping.order.domain.KakaopayPayment;
import com.teemo.shopping.order.enums.PaymentStates;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.observer.PaymentStateUpdatePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KakaopayPaymentRefundService {
    @Autowired
    private PaymentRepository<KakaopayPayment> kakaopayPaymentRepository;

    @Autowired
    private PaymentStateUpdatePublisher paymentStateUpdatePublisher;
    @Transactional
    public void onRefund(Long paymentId, KakaopayAPICancelResponse response) {
        KakaopayPayment kakaopayPayment;
        kakaopayPayment = kakaopayPaymentRepository.findById(paymentId).get();
        var amount = response.getAmount().getTotal();
        kakaopayPayment.setRefundedAmount(kakaopayPayment.getRefundedAmount() + amount);
        if(!kakaopayPayment.getRefundedAmount().equals(kakaopayPayment.getAmount())) {
            kakaopayPayment.setState(PaymentStates.PARTIAL_REFUNDED);
        } else {
            kakaopayPayment.setState(PaymentStates.REFUNDED);
        }
        paymentStateUpdatePublisher.publish(kakaopayPayment.getPaymentStateUpdateSubscriberTypes(), kakaopayPayment.getId());
    }
}