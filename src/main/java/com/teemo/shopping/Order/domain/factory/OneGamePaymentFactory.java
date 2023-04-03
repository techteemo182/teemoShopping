package com.teemo.shopping.Order.domain.factory;

import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.dto.OneGamePaymentFactoryContext;
import java.util.Optional;

public interface OneGamePaymentFactory extends PaymentFactory<OneGamePaymentFactoryContext> {

    Optional<Payment> create(OneGamePaymentFactoryContext context);
    PaymentMethod getTargetPaymentMethod();
}
