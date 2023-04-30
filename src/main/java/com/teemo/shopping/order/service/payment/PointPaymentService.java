package com.teemo.shopping.order.service.payment;

import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.domain.PointPayment;
import com.teemo.shopping.order.enums.PaymentMethods;
import com.teemo.shopping.order.enums.PaymentStates;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.order.service.paymentSubscriber.PaymentStatePublisher;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointPaymentService extends PaymentService {

    @Autowired
    private PaymentRepository<PointPayment> pointPaymentRepository;
    @Autowired
    private PaymentStatePublisher paymentStatePublisher;


    public PaymentMethods getPaymentMethod() {
        return PaymentMethods.POINT;
    }

    @Override
    public Payment create(OrderCreateContext context) throws Exception {
        PointPayment pointPayment = PointPayment.builder()
            .order(context.getOrder())
            .account(context.getAccount())
            .amount(context.getAmount())
            .method(PaymentMethods.POINT)
            .state(PaymentStates.PENDING)
            .build();
        return pointPayment;
    }


    @Override
    @Transactional
    public void refund(Long paymentId, int amount) {    // 부분 취소 가능
        PointPayment payment = pointPaymentRepository.findById(paymentId).get();
        if (!payment.getState().equals(PaymentStates.PENDING)) {
            throw new IllegalStateException("결제 가능한 상태가 아님.");
        }
        if(payment.getRefundableAmount() < amount) { // 환불 가능한 금액이 환불할 금액보다 큼
            throw new IllegalStateException("환불 할 금액이 환불 가능한 금액보다 큼.");
        }
        payment.updateRefundedAmount(payment.getRefundedAmount() + amount);
        payment.updateState(payment.getAmount() == payment.getRefundedAmount() ? PaymentStates.REFUNDED : PaymentStates.PARTIAL_REFUNDED);   // 환불 상태 결정
        paymentStatePublisher.publish(paymentId);
    }

    @Override
    public void pay(Long paymentId) {
        PointPayment pointPayment = pointPaymentRepository.findById(paymentId).get();
        if (pointPayment.getState() != PaymentStates.PENDING) {
            throw new IllegalStateException("결제 가능한 상태가 아님.");
        }
        try {
            int point = pointPayment.getAmount();
            int accountPoint = pointPayment.getAccount().getPoint();
            int availablePoint = Math.min(point, accountPoint);
            if (pointPayment.getAmount() == 0 || pointPayment.getAccount().getPoint() == 0) {
                throw new IllegalStateException("사용가능 포인트가 없습니다.");
            }
            pointPayment.updateState(PaymentStates.SUCCESS);
        } catch(Exception e) {
            pointPayment.updateState(PaymentStates.CANCEL);
        }
        paymentStatePublisher.publish(paymentId);
    }

    @Override
    public Class<? extends Payment> getPaymentClass() {
        return PointPayment.class;
    }
}
