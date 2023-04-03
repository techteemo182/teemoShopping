package com.teemo.shopping.Order.service.payment_factory;

import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.PointPayment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.repository.PaymentRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@org.springframework.core.annotation.Order(100)
public class PointPaymentFactory implements AllProductPaymentFactory {

    @Autowired
    private PaymentRepository<PointPayment> pointPaymentRepository;

    public PaymentMethod getTargetPaymentMethod() {
        return PaymentMethod.POINT;
    }

    @Override
    public Optional<Payment> create(AllGameProductContext context) {
        int totalRemainPrice = context.getTotalRemainPrice();
        int pointPrice = context.getPoint();
        if (totalRemainPrice > context.getPoint()) {
            throw new RuntimeException();
        }
        PointPayment payment = PointPayment.builder().status(PaymentStatus.SUCCESS)
            .price(pointPrice).build();
        pointPaymentRepository.save(payment);
        context.setTotalRemainPrice(totalRemainPrice - pointPrice);
        return Optional.of(payment);
    }

}
