package com.teemo.shopping.order.service.payment;

import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class PaymentService {

    @Autowired
    protected PaymentRepository<Payment> paymentRepository;

    public abstract Payment create(OrderCreateContext context) throws Exception;

    public abstract void refund(Long paymentId, int amount);

    public abstract void pay(Long paymentId);

    public abstract Class<? extends Payment> getPaymentClass();
}

