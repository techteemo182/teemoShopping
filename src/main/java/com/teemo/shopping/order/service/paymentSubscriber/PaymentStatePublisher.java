package com.teemo.shopping.order.service.paymentSubscriber;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatePublisher {
    @Autowired
    @Lazy
    List<PaymentStateSubscriber> subscribers;
    public void publish(Long paymentId) {
        for(var subscriber : subscribers) {
            subscriber.subscribe(paymentId);
        }
    }

}

