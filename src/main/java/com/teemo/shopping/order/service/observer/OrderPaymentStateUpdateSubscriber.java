package com.teemo.shopping.order.service.observer;

import com.teemo.shopping.order.enums.PaymentStateUpdateSubscriberTypes;
import com.teemo.shopping.order.service.OrderStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class OrderPaymentStateUpdateSubscriber implements PaymentStateUpdateSubscriber {

    @Autowired
    @Lazy
    private OrderStateService orderStateService;

    @Override
    public PaymentStateUpdateSubscriberTypes getType() {
        return PaymentStateUpdateSubscriberTypes.ORDER;
    }

    @Override
    public void update(Long paymentId) {
        orderStateService.orderStateTransition(paymentId);
    }
}
