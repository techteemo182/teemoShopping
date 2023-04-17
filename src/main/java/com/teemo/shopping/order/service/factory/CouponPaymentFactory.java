package com.teemo.shopping.order.service.factory;

import com.teemo.shopping.order.domain.CouponPayment;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.service.CouponPaymentService;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.order.service.parameter.CouponPaymentCreateParameter;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CouponPaymentFactory implements PaymentFactory {

    @Autowired
    private CouponPaymentService couponPaymentService;

    @Override
    public Optional<Payment> create(OrderCreateContext context) {
        return couponPaymentService.add(
            CouponPaymentCreateParameter.builder().coupon(context.getCoupon())
                .game(context.getGame()).account(context.getAccount()).order(context.getOrder())
                .amount(context.getAmount()).build());
    }

    @Override
    public Class<? extends Payment> getPaymentClass() {
        return CouponPayment.class;
    }
}
