package com.teemo.shopping.payment.service;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.service.AccountService;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.payment.domain.Payment;
import com.teemo.shopping.payment.domain.PointPayment;
import com.teemo.shopping.payment.domain.enums.PaymentStates;
import com.teemo.shopping.payment.observer.PaymentStateUpdatePublisher;
import com.teemo.shopping.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointPaymentService extends PaymentService {

    @Autowired
    private PaymentRepository<PointPayment> pointPaymentRepository;

    @Autowired
    private PaymentStateUpdatePublisher paymentStateUpdatePublisher;
    @Autowired
    private AccountService AccountService;

    @Override
    @Transactional
    public void refund(Long paymentId, int amount) {    // 부분 취소 가능
        PointPayment payment = pointPaymentRepository.findById(paymentId).get();
        if (!payment.getState().equals(PaymentStates.PENDING)) {
            throw new IllegalStateException("결제 가능한 상태가 아님.");
        }
        if (payment.getRefundableAmount() < amount) { // 환불 가능한 금액이 환불할 금액보다 큼
            throw new IllegalStateException("환불 할 금액이 환불 가능한 금액보다 큼.");
        }
        payment.setRefundedAmount(payment.getRefundedAmount() + amount);
        payment.setState(payment.getAmount() == payment.getRefundedAmount() ? PaymentStates.REFUNDED
            : PaymentStates.PARTIAL_REFUNDED);   // 환불 상태 결정
        paymentStateUpdatePublisher.publish(payment.getPaymentStateUpdateSubscriberTypes(),
            payment.getId());
    }

    @Override
    @Transactional
    public void pay(Long paymentId) {
        PointPayment pointPayment = pointPaymentRepository.findById(paymentId).get();
        if (pointPayment.getState() != PaymentStates.PENDING) {
            throw new IllegalStateException("결제 가능한 상태가 아님.");
        }
        try {
            int point = pointPayment.getAmount();
            checkHavePointElseThrow(pointPayment.getAccount(), point);
            AccountService.usePoint(pointPayment.getAccount().getId(), point);
            pointPayment.setState(PaymentStates.SUCCESS);
        } catch (Exception e) {
            pointPayment.setState(PaymentStates.CANCEL);
        }
        paymentStateUpdatePublisher.publish(pointPayment.getPaymentStateUpdateSubscriberTypes(),
            pointPayment.getId());
    }
    private void checkHavePointElseThrow(Account account, int amount) {
        if(account.getPoint() < amount) {
            throw new IllegalStateException("포인트가 부족 합니다.");
        }
    }

    @Override
    public Class<? extends Payment> getTarget() {
        return PointPayment.class;
    }
}

