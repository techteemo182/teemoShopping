package com.teemo.shopping.Order;

import com.teemo.shopping.account.Account;
import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
    public Order(Account account, double totalPrice) {
        this.account = account;
        this.totalPrice = totalPrice;
    }


    @ManyToOne
    Account account;

    @Column
    private double totalPrice;

    /**
     * 주문 상태
     *  CANCEL : 취소
     *  PENDING : 기다리는 중
     *  SUCCESS : 성공
     */
    @Enumerated
    private OrderStatus status;
}

