package com.teemo.shopping.Order.domain;

import com.teemo.shopping.Order.domain.enums.OrderStatus;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.core.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
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

    @Builder
    public Order(Account account, int totalPrice, OrderStatus status) {
        this.account = account;
        this.totalPrice = totalPrice;
        this.status = status;
    }


    @ManyToOne
    Account account;

    @Column
    private int totalPrice;

    /**
     * 주문 상태
     *  CANCEL : 취소
     *  PENDING : 기다리는 중
     *  SUCCESS : 성공
     */
    @Enumerated
    private OrderStatus status;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrdersGames> ordersGames = new ArrayList<>();
    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
}

