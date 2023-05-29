package com.teemo.shopping.payment.service.payment_tree;

import com.teemo.shopping.payment.domain.Payment;
import com.teemo.shopping.payment.service.factory.OrderContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class PaymentGroup implements PaymentNode {
    private List<PaymentNode> paymentNodes = new ArrayList<>();

    @Override
    public List<Payment> create(OrderContext context) {
        List<Payment> payments = new ArrayList<>();
        Optional<List<Payment>> lastChildPayments = Optional.empty();
        for(var paymentNode : paymentNodes) {
            context = getContext(context, lastChildPayments);
            List<Payment> childPayments = paymentNode.create(context);
            payments.addAll(childPayments);
            lastChildPayments = Optional.of(childPayments);
        }
        throwIf(payments);
        return payments;
    }

    public void addPaymentNode(PaymentNode paymentNode) {
        paymentNodes.add(paymentNode);
    }

    protected abstract OrderContext getContext(OrderContext context, Optional<List<Payment>> childPayments);
    protected abstract void throwIf(List<Payment> payments);
}
