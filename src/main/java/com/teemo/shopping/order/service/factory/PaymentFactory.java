package com.teemo.shopping.order.service.factory;

import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import java.util.Optional;

public interface PaymentFactory {
    Optional<Payment> create(OrderCreateContext context);
    Class<? extends Payment> getPaymentClass();
}
