package com.teemo.shopping.Order;

import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AttributeOverride(name = "id", column = @Column(name = "payments_id"))
@Table(
    name = "payments"
)
public class Payment extends BaseEntity {

    @Builder
    protected Payment(PaymentMethod method, double price, String info) {
        this.method = method;
        this.price = price;
        this.info = info;
    }

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column
    private double price;

    @Column
    private String info;
}
