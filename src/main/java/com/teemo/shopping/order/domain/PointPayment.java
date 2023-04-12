package com.teemo.shopping.order.domain;

import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.enums.PaymentMethod.Values;
import com.teemo.shopping.order.enums.PaymentStatus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "point_payments"
)
@DiscriminatorValue(Values.POINT)
public class PointPayment extends AllProductPayment {

    @Builder
    public PointPayment(int amount, PaymentStatus status, Order order) {
        super(amount, status, order, PaymentMethod.POINT);
    }
}