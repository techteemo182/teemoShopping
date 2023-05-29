package com.teemo.shopping.payment.service.payment_tree;

import com.teemo.shopping.payment.domain.Payment;
import com.teemo.shopping.payment.service.factory.OrderContext;

import java.util.ArrayList;
import java.util.List;

public interface PaymentNode {
    List<Payment> create(OrderContext context);
}
