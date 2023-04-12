package com.teemo.shopping.order.service;

import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.dto.PaymentStatusUpdateObserverContext;
import com.teemo.shopping.order.dto.PaymentRefundParameter;
import com.teemo.shopping.order.dto.payment_create_param.PaymentCreateParam;
import com.teemo.shopping.core.layer.ServiceLayer;
import com.teemo.shopping.core.observer.Observer;
import com.teemo.shopping.core.observer.Subject;
import java.util.ArrayList;
import java.util.Optional;

public abstract class PaymentService<T extends PaymentCreateParam> implements ServiceLayer, Subject<PaymentStatusUpdateObserverContext, Observer<PaymentStatusUpdateObserverContext>> {


    /**
     *
     * @param param
     * context를 이용하여 payment 생성
     * @return 생성한 Payment 반환
     */


    //Context
    abstract Optional<Long> create(T param);    // 지불
    abstract void refund(PaymentRefundParameter parameter);    // 환불
    abstract Class<? extends Payment> getTargetPaymentClass();
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
