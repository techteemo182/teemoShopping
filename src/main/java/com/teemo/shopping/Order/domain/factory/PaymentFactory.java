package com.teemo.shopping.Order.domain.factory;

import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import java.util.Optional;

public interface PaymentFactory<C> {
    Optional<Payment> create(C context);
    PaymentMethod getTargetPaymentMethod();
}
