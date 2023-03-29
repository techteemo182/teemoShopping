package com.teemo.shopping.Order;

import com.teemo.shopping.account.Account;
import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@AttributeOverride(name = "id", column = @Column(name = "orders_id"))
@Table(
    name = "orders"
)
public class Order extends BaseEntity {
    @ManyToOne
    Account account;    // onwer

    @Column
    private double totalPrice;
}
