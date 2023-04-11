package com.teemo.shopping.order.dto;

import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.core.observer.ObserverContext;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentStatusUpdateObserverContext extends ObserverContext {

    private final Payment payment;
}
