package com.teemo.shopping.Order.domain;

import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.core.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AttributeOverride(name = "id", column = @Column(name = "payments_id"))
@Table(
    name = "payments"
)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("null")
@Inheritance(strategy = InheritanceType.JOINED)
@Embeddable
public class Payment extends BaseEntity {

    protected Payment(int amount, PaymentStatus status, Order order,PaymentMethod method) {
        this.amount = amount;
        this.status = status;
        this.order = order;
        this.method = method;
    }

    @Column
    /**
     *  결제의 양 또는 환불의 양
     */
    @Range(min = 0)
    private int amount;

    @Formula("amount - refunded_amount")
    /**
     *  환불 가능 금액
     */
    private int refundableAmount;

    @Column
    @NotNull
    private int refundedAmount;

    /**
     *  결제 상태
     *  CANCEL : 취소
     *  PENDING : 기다리는 중
     *  SUCCESS : 성공
     *  REFUND : 환불됨
     *  PARTIAL_REFUND : 부분환불됨
     *
     *  생명주기:
     *  PENDING -> SUCCESS -> (PARTIAL_REFUND) -> REFUND
     *          -> CANCEL
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus status;

    @ManyToOne
    @NotNull
    private Order order;

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }
    public void updateRefundedPoint(int refundedPrice) {this.refundedAmount = refundedPrice;}
}