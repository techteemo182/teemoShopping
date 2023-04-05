package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.PointPayment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.AllGamePaymentServiceContext;
import com.teemo.shopping.Order.repository.PaymentRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(100)
public class PointPaymentService extends AllGamePaymentService {

    @Autowired
    private PaymentRepository<PointPayment> pointPaymentRepository;

    public PaymentMethod getTargetPaymentMethod() {
        return PaymentMethod.POINT;
    }

    @Override
    public Optional<Payment> create(AllGamePaymentServiceContext context) {
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

}
