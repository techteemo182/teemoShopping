package com.teemo.shopping.order.service.observer;

import com.teemo.shopping.order.enums.PaymentStateUpdateSubscriberTypes;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentStateUpdatePublisher {

    Map<PaymentStateUpdateSubscriberTypes, PaymentStateUpdateSubscriber> listenerMap = new EnumMap<>(
        PaymentStateUpdateSubscriberTypes.class);

    @Autowired
    protected PaymentStateUpdatePublisher(
        List<PaymentStateUpdateSubscriber> paymentStateUpdateSubscribers) {
        for (var paymentStateListener : paymentStateUpdateSubscribers) {
            listenerMap.put(paymentStateListener.getType(), paymentStateListener);
        }
    }

    public void publish(PaymentStateUpdateSubscriberTypes listenerType, Long paymentId) {
        if (listenerType.equals(PaymentStateUpdateSubscriberTypes.NONE)) {
            return;
        }
        listenerMap.get(listenerType).update(paymentId);
    }

}
