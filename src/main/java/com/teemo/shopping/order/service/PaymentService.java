package com.teemo.shopping.order.service;

import com.teemo.shopping.core.layer.ServiceLayer;
import com.teemo.shopping.core.observer.Observer;
import com.teemo.shopping.core.observer.Subject;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.service.context.PaymentStatusUpdateObserverContext;
import com.teemo.shopping.order.service.parameter.PaymentCreateParameter;
import com.teemo.shopping.order.service.parameter.PaymentRefundParameter;
import java.util.ArrayList;
import java.util.Optional;

public abstract class PaymentService<T extends PaymentCreateParameter> implements ServiceLayer, Subject<PaymentStatusUpdateObserverContext, Observer<PaymentStatusUpdateObserverContext>> {
    public abstract Optional<Payment> create(T parameter);    // 지불
    public abstract void refund(PaymentRefundParameter parameter);    // 환불
    public abstract Class<? extends Payment> getPaymentClass();
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

