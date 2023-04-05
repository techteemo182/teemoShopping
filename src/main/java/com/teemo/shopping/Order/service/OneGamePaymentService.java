package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.dto.OneGamePaymentServiceContext;
import java.util.Optional;

public abstract class OneGamePaymentService extends PaymentService<OneGamePaymentServiceContext> {

    abstract Optional<Payment> create(OneGamePaymentServiceContext context);
    abstract PaymentMethod getTargetPaymentMethod();
}
