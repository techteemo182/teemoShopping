package com.teemo.shopping.payment.observer;

import com.teemo.shopping.payment.domain.enums.PaymentStateUpdateSubscriberTypes;

public interface PaymentStateUpdateSubscriber {

    PaymentStateUpdateSubscriberTypes getType();

    void update(Long paymentId);
}
