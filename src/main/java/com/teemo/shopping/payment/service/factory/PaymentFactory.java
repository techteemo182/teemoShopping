package com.teemo.shopping.payment.service.factory;

import com.teemo.shopping.payment.domain.Payment;

public interface PaymentFactory<Context> {
    Payment create(Context context);
}
