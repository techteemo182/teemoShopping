package com.teemo.shopping.Order.service.payment_factory;

import com.teemo.shopping.Order.domain.Payment;
import java.util.Optional;

public interface SingleProductPaymentFactory extends ProductPaymentFactory {

    Optional<Payment> create(SingleGameProductContext context);
}
