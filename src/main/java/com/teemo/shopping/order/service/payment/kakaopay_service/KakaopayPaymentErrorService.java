package com.teemo.shopping.order.service.payment.kakaopay_service;

import com.teemo.shopping.order.domain.KakaopayPayment;
import com.teemo.shopping.order.enums.PaymentStates;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.observer.PaymentStateUpdatePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KakaopayPaymentErrorService {
    @Autowired
    private PaymentRepository<KakaopayPayment> kakaopayPaymentRepository;
    @Autowired
    private PaymentStateUpdatePublisher paymentStateUpdatePublisher;

    @Transactional
    public void onPayError(Long paymentId) {
        KakaopayPayment kakaopayPayment;
        try {
            kakaopayPayment = kakaopayPaymentRepository.findById(paymentId).get();
        } catch (Exception e) {
            throw new IllegalStateException("존재하지 않는 paymentId");
        }
        kakaopayPayment.setState(PaymentStates.CANCEL);
        paymentStateUpdatePublisher.publish(kakaopayPayment.getPaymentStateUpdateSubscriberTypes(), kakaopayPayment.getId());
    }
    @Transactional
    public void onRefundError(Long paymentId) {
        KakaopayPayment kakaopayPayment;
        kakaopayPayment = kakaopayPaymentRepository.findById(paymentId).get();
        if(kakaopayPayment.getRefundedAmount() == 0) {
            kakaopayPayment.setState(PaymentStates.SUCCESS);
        } else if(kakaopayPayment.getRefundableAmount() != 0) {
            kakaopayPayment.setState(PaymentStates.PARTIAL_REFUNDED);
        } else {
            kakaopayPayment.setState(PaymentStates.REFUNDED);
        }
        paymentStateUpdatePublisher.publish(kakaopayPayment.getPaymentStateUpdateSubscriberTypes(), kakaopayPayment.getId());
    }
}
