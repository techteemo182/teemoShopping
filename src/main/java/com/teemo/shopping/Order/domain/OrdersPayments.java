package com.teemo.shopping.Order.domain;

import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class OrdersPayments extends BaseEntity {

    @Builder
    protected OrdersPayments(Order order, Payment payment) {
        this.order = order;
        this.payment = payment;
    }

    @ManyToOne
    @JoinColumn(name = "orders_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "payments_id")
    private Payment payment;
}
