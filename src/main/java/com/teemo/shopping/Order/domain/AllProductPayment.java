package com.teemo.shopping.Order.domain;

import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
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
