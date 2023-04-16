package com.teemo.shopping.order.domain;

import com.teemo.shopping.order.enums.OrderStatus;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.core.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
    protected Order(Account account, int totalPrice, OrderStatus status) {
        this.account = account;
        this.totalPrice = totalPrice;
        this.status = status;
    }
    @Override
    public boolean equals(Object obj) {
        Order target = (Order)obj;
        return target.getId().equals(getId());
    }

    @ManyToOne
    @NotNull
    private Account account;

    @Column
    @Range(min = 0)
    private int totalPrice;

    /**
     * 주문 상태
     *  CANCEL : 취소
     *  PENDING : 기다리는 중
     *  SUCCESS : 성공
     */
    @Enumerated
    @NotNull
    private OrderStatus status;
    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
}

