package com.teemo.shopping.Order.service.payment_factory;

import com.teemo.shopping.Order.domain.enums.PaymentMethod;

public interface ProductPaymentFactory {

    PaymentMethod getTargetPaymentMethod();
}
