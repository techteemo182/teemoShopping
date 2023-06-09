package com.teemo.shopping.order.domain;

import com.teemo.shopping.order.domain.enums.OrderStates;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.payment.domain.Payment;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor
@Entity
@AttributeOverride(name = "id", column = @Column(name = "orders_id"))
@Table(
    name = "orders"
)

public class Order extends BaseEntity {

    @Builder
    protected Order(Account account, int totalPrice, OrderStates state) {
        this.account = account;
        this.totalPrice = totalPrice;
        this.state = state;
    }
    @Override
    public boolean equals(Object obj) {
        Order target = (Order)obj;
        return target.getId().equals(getId());
    }

    @ManyToOne
    @NotNull
    @JoinColumn(name = "accounts_id")
    private Account account;

    @Column
    @Range(min = 0)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @NotNull
    private OrderStates state;

    @OneToMany(fetch = FetchType.LAZY, cascade = {
        CascadeType.PERSIST
    })
    @JoinTable(name = "orders_payments")
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = {
        CascadeType.PERSIST
    })
    private List<OrdersGames> ordersGames = new ArrayList<>();
    public void updateState(OrderStates state) {
        this.state = state;
    }
}

