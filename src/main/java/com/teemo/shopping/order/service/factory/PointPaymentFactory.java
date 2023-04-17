package com.teemo.shopping.order.service.factory;

import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.domain.PointPayment;
import com.teemo.shopping.order.service.PointPaymentService;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.order.service.parameter.PointPaymentCreateParameter;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PointPaymentFactory implements PaymentFactory {

    @Autowired
    private PointPaymentService pointPaymentService;

    @Override
    public Optional<Payment> create(OrderCreateContext context) {
        return pointPaymentService.add(
            PointPaymentCreateParameter.builder().account(context.getAccount())
                .order(context.getOrder()).amount(context.getAmount())
                .availablePoint(context.getPoint()).build());
    }

    @Override
    public Class<? extends Payment> getPaymentClass() {
        return PointPayment.class;
    }
}
