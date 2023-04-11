package com.teemo.shopping.order.domain;

import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.enums.PaymentStatus;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public class AllProductPayment extends Payment {
    protected AllProductPayment(int amount, PaymentStatus status,
        Order order, PaymentMethod method) {
        super(amount, status, order, method);
    }
}
