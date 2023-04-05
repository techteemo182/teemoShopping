package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.dto.OrderUpdateObserverContext;
import com.teemo.shopping.Order.dto.PaymentCancelParameter;
import com.teemo.shopping.core.layer.ServiceLayer;
import com.teemo.shopping.core.observer.Observer;
import com.teemo.shopping.core.observer.Subject;
import java.util.ArrayList;
import java.util.Optional;

public abstract class PaymentService<C> implements ServiceLayer, Subject<OrderUpdateObserverContext, Observer<OrderUpdateObserverContext>> {

    abstract Optional<Payment> create(C context);
    abstract void cancel(PaymentCancelParameter parameter);
    abstract PaymentMethod getTargetPaymentMethod();
    private ArrayList<Observer<OrderUpdateObserverContext>> observers = new ArrayList<>();
    @Override
    public void registerObserver(Observer<OrderUpdateObserverContext> observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterObserver(Observer<OrderUpdateObserverContext> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(OrderUpdateObserverContext context) {
        observers.stream().forEach((observer) -> observer.onUpdate(context));
    }
}

