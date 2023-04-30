package com.teemo.shopping.order.service.paymentSubscriber;

public interface PaymentStateSubscriber {

    void subscribe(Long paymentId);
}
