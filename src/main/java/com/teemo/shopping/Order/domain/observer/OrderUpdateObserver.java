package com.teemo.shopping.Order.domain.observer;

import com.teemo.shopping.Order.dto.OrderUpdateObserverContext;
import com.teemo.shopping.Order.service.OrderService;
import com.teemo.shopping.core.observer.Observer;
import com.teemo.shopping.core.observer.Subject;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderUpdateObserver implements Observer<OrderUpdateObserverContext> {

    @Autowired
    private OrderService orderService;
    @Autowired
    private List<Subject<OrderUpdateObserverContext, Observer<OrderUpdateObserverContext>>> subjects = new ArrayList<>();

    OrderUpdateObserver() {
        subjects.stream().forEach((subject) -> subject.registerObserver(this));
    }
    public void onUpdate(OrderUpdateObserverContext orderUpdateObserverContext) {
        com.teemo.shopping.Order.domain.Order order = orderUpdateObserverContext.getPayment()
            .getOrder();
        orderService.updateOrder(order.getId());
    }
}
