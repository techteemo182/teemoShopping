package com.teemo.shopping.Order;

import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    protected Payment(String method, double price, String info) {
        this.method = method;
        this.price = price;
        this.info = info;
    }

    @Column
    String method;

    @Column
    double price;

    @Column
    String info;
}
