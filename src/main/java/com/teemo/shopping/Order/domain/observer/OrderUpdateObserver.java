package com.teemo.shopping.Order.domain.observer;

import com.teemo.shopping.Order.dto.PaymentStatusUpdateObserverContext;
import com.teemo.shopping.Order.service.OrderService;
import com.teemo.shopping.core.observer.Observer;
import com.teemo.shopping.core.observer.Subject;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderUpdateObserver implements Observer<PaymentStatusUpdateObserverContext> {

    @Autowired
    private OrderService orderService;
    @Autowired
    private List<Subject<PaymentStatusUpdateObserverContext, Observer<PaymentStatusUpdateObserverContext>>> subjects = new ArrayList<>();

    OrderUpdateObserver() {
        subjects.stream().forEach((subject) -> subject.registerObserver(this));
    }
    public void onUpdate(PaymentStatusUpdateObserverContext paymentStatusUpdateObserverContext) {
        com.teemo.shopping.Order.domain.Order order = paymentStatusUpdateObserverContext.getPayment()
            .getOrder();
        orderService.updateOrder(order.getId());
    }
}
