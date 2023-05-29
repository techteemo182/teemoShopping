package com.teemo.shopping.payment.service.factory;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.payment.domain.Payment;
import com.teemo.shopping.payment.domain.PointPayment;

import java.util.NoSuchElementException;

public class PointPaymentFactory implements PaymentFactory<OrderContext>{

    @Override
    public Payment create(OrderContext orderContext) {
        Account account = orderContext.getAccount().get();
        int amount = orderContext.getAmount().get();
        return PointPayment.builder()
                .account(account).amount(amount).build();
    }
}
