package com.teemo.shopping.payment.service;

import com.teemo.shopping.payment.domain.Payment;
import com.teemo.shopping.payment.domain.enums.PaymentMethods;
import com.teemo.shopping.payment.repository.PaymentRepository;
import com.teemo.shopping.order.service.context.OrderCreateContext;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class PaymentService {

    public abstract void refund(Long paymentId, int amount);

    public abstract void pay(Long paymentId);
    public abstract Class<? extends Payment> getTarget();       //improve: Inheritance Entity 를 식별하는 다른 방법이 있긴 하나 1. Enum
}


