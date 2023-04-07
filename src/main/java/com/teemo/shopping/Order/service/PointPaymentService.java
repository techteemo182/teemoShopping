package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.PointPayment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.PaymentCreateContext;
import com.teemo.shopping.Order.dto.PaymentRefundParameter;
import com.teemo.shopping.Order.repository.PaymentRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("forAllProduct")
@org.springframework.core.annotation.Order(100)
public class PointPaymentService extends PaymentService {

    @Autowired
    private PaymentRepository<PointPayment> pointPaymentRepository;

    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.POINT;
    }

    @Override
    public Optional<Payment> create(PaymentCreateContext context) {
        int remainPrice = context.getRemainPrice();
        int pointPrice = context.getPoint();
        com.teemo.shopping.Order.domain.Order order = context.getOrder();
        if (remainPrice < pointPrice) {
            throw new RuntimeException();
        }
        PointPayment payment = PointPayment.builder().status(PaymentStatus.SUCCESS)
            .price(pointPrice).order(order).build();
        pointPaymentRepository.save(payment);
        context.setRemainPrice(remainPrice - pointPrice);
        return Optional.of(payment);
    }

    @Override
    void refund(PaymentRefundParameter parameter) {    // 부분 취소 가능
        PointPayment payment = pointPaymentRepository.findById(parameter.getPaymentId()).get();
        int refundPrice = parameter.getPrice();
        if(payment.getPrice() - payment.getRefundedPrice() < refundPrice) { // 환불 가능한 금액이 환불할 금액보다 큼
            throw new RuntimeException();
        }
        payment.updateRefundedPoint(payment.getRefundedPrice() + refundPrice);
        payment.updateStatus(payment.getPrice() == payment.getRefundedPrice() ? PaymentStatus.REFUNDED : PaymentStatus.PARTIAL_REFUNDED);   // 환불 상태 결정
    }
}
