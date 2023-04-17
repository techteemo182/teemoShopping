package com.teemo.shopping.order.service.factory;

import com.teemo.shopping.order.domain.DiscountPayment;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.service.DiscountPaymentService;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.order.service.parameter.DiscountPaymentCreateParameter;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiscountPaymentFactory implements PaymentFactory {

    @Autowired
    private DiscountPaymentService discountPaymentService;

    @Override
    public Optional<Payment> create(OrderCreateContext context) {
        return discountPaymentService.add(
            DiscountPaymentCreateParameter.builder().account(context.getAccount())
                .order(context.getOrder()).amount(context.getAmount()).game(context.getGame())
                .build());
    }

    @Override
    public Class<? extends Payment> getPaymentClass() {
        return DiscountPayment.class;
    }
}
