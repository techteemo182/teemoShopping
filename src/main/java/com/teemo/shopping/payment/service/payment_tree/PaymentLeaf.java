package com.teemo.shopping.payment.service.payment_tree;

import com.teemo.shopping.payment.domain.Payment;
import com.teemo.shopping.payment.service.factory.OrderContext;
import com.teemo.shopping.payment.service.factory.PaymentFactory;

import java.util.List;
import java.util.Optional;

public class PaymentLeaf implements PaymentNode {

    PaymentFactory<OrderContext> paymentFactory;
    Optional<Integer> amount;

    public PaymentLeaf(PaymentFactory<OrderContext> paymentFactory, Optional<Integer> amount) {
        this.paymentFactory = paymentFactory;
        this.amount = amount;
    }

    @Override
    public List<Payment> create(OrderContext context) {
        context.setAmount(amount);
        return List.of(paymentFactory.create(context));
    }
}
