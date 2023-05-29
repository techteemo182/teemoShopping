package com.teemo.shopping.payment.service;

import com.teemo.shopping.game.repository.GameRepository;
import com.teemo.shopping.payment.domain.DiscountPayment;
import com.teemo.shopping.payment.domain.Payment;
import com.teemo.shopping.payment.domain.enums.PaymentStates;
import com.teemo.shopping.payment.observer.PaymentStateUpdatePublisher;
import com.teemo.shopping.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountPaymentService extends PaymentService {


    @Autowired
    private PaymentRepository<DiscountPayment> discountPaymentRepository;
    @Autowired
    private PaymentStateUpdatePublisher paymentStateUpdatePublisher;
    @Autowired
    private GameRepository gameRepository;

    @Override
    public void refund(Long paymentId, int amount) {    // 부분 취소 불가능
        DiscountPayment payment = discountPaymentRepository.findById(paymentId).get();
        if (payment.getState().equals(PaymentStates.SUCCESS)) {
            throw new IllegalStateException("환불 가능한 상태가 아님.");
        }
        payment.setRefundedAmount(payment.getAmount());
        payment.setState(PaymentStates.REFUNDED);
        paymentStateUpdatePublisher.publish(payment.getPaymentStateUpdateSubscriberTypes(), payment.getId());
    }

    @Override
    public void pay(Long paymentId) {
        DiscountPayment payment = discountPaymentRepository.findById(paymentId).get();
        if (!payment.getState().equals(PaymentStates.PENDING)) {
            throw new IllegalStateException("결제 가능한 상태가 아님.");
        }
        payment.setState(PaymentStates.SUCCESS);
        paymentStateUpdatePublisher.publish(payment.getPaymentStateUpdateSubscriberTypes(), payment.getId());
    }

    @Override
    public Class<? extends Payment> getTarget() {
        return DiscountPayment.class;
    }
}
