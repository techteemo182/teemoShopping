package com.teemo.shopping.Order.domain.factory;

import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.PointPayment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.AllGamePaymentFactoryContext;
import com.teemo.shopping.Order.repository.PaymentRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
public class PointPaymentFactory implements AllGamePaymentFactory {

    @Autowired
    private PaymentRepository<PointPayment> pointPaymentRepository;

    public PaymentMethod getTargetPaymentMethod() {
        return PaymentMethod.POINT;
    }

    @Override
    public Optional<Payment> create(AllGamePaymentFactoryContext context) {
        int remainPrice = context.getRemainPrice();
        int pointPrice = context.getPoint();
        if (remainPrice > pointPrice) {
            throw new RuntimeException();
        }
        PointPayment payment = PointPayment.builder().status(PaymentStatus.SUCCESS)
            .price(pointPrice).build();
        pointPaymentRepository.save(payment);
        context.setRemainPrice(remainPrice - pointPrice);
        return Optional.of(payment);
    }

}
