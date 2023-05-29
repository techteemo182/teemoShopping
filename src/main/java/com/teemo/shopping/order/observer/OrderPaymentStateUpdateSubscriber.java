package com.teemo.shopping.order.observer;

import com.teemo.shopping.order.service.OrderStateService;
import com.teemo.shopping.payment.domain.enums.PaymentStateUpdateSubscriberTypes;
import com.teemo.shopping.payment.observer.PaymentStateUpdateSubscriber;
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
