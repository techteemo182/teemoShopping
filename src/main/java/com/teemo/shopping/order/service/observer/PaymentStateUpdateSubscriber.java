package com.teemo.shopping.order.service.observer;

import com.teemo.shopping.order.enums.PaymentStateUpdateSubscriberTypes;

public interface PaymentStateUpdateSubscriber {

    PaymentStateUpdateSubscriberTypes getType();

    void update(Long paymentId);
}
