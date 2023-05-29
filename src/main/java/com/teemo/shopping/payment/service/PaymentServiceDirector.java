package com.teemo.shopping.payment.service;

import com.teemo.shopping.payment.domain.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceDirector {   // 이런 패턴이 있나
    private Map<Class<? extends Payment>, PaymentService> classPaymentServiceMap = new HashMap<>();
    @Autowired
    protected PaymentServiceDirector(List<PaymentService> paymentServices) {
        for(var paymentService : paymentServices) {
            classPaymentServiceMap.put(paymentService.getTarget(), paymentService);
        }
    }

    public void pay(Payment payment) {
        classPaymentServiceMap.get(payment.getClass()).pay(payment.getId());
    }
    public void refund(Payment payment, int amount) {
        classPaymentServiceMap.get(payment.getClass()).refund(payment.getId(), amount);
    }
}
