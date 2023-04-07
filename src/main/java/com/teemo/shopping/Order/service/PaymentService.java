package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.dto.PaymentStatusUpdateObserverContext;
import com.teemo.shopping.Order.dto.PaymentRefundParameter;
import com.teemo.shopping.Order.dto.PaymentCreateContext;
import com.teemo.shopping.core.layer.ServiceLayer;
import com.teemo.shopping.core.observer.Observer;
import com.teemo.shopping.core.observer.Subject;
import java.util.ArrayList;
import java.util.Optional;

public abstract class PaymentService implements ServiceLayer, Subject<PaymentStatusUpdateObserverContext, Observer<PaymentStatusUpdateObserverContext>> {

    abstract Optional<Payment> create(PaymentCreateContext context);    // 지불
    abstract void refund(PaymentRefundParameter parameter);    // 환불
    abstract PaymentMethod getPaymentMethod();
    private ArrayList<Observer<PaymentStatusUpdateObserverContext>> observers = new ArrayList<>();  // 옵저버
    @Override
    public void registerObserver(Observer<PaymentStatusUpdateObserverContext> observer) {   // 옵저버 등록
        observers.add(observer);
    }

    @Override
    public void unregisterObserver(Observer<PaymentStatusUpdateObserverContext> observer) {     // 옵저버 제거
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(PaymentStatusUpdateObserverContext context) {   // 변경을 알리기
        observers.stream().forEach((observer) -> observer.onUpdate(context));
    }
}

