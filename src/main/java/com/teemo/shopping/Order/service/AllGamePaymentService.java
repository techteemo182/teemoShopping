package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.dto.AllGamePaymentServiceContext;
import java.util.Optional;

public abstract class AllGamePaymentService extends PaymentService<AllGamePaymentServiceContext> {

    abstract Optional<Payment> create(AllGamePaymentServiceContext context);
    abstract PaymentMethod getTargetPaymentMethod();
}
