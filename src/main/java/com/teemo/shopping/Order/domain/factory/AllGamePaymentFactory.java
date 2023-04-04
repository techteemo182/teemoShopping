package com.teemo.shopping.Order.domain.factory;

import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.dto.AllGamePaymentFactoryContext;
import java.util.Optional;

public interface AllGamePaymentFactory extends PaymentFactory<AllGamePaymentFactoryContext>{

    Optional<Payment> create(AllGamePaymentFactoryContext context);
    PaymentMethod getTargetPaymentMethod();
}
