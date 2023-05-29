package com.teemo.shopping.payment.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.payment.domain.enums.PaymentMethods;
import com.teemo.shopping.payment.domain.enums.PaymentStateUpdateSubscriberTypes;
import com.teemo.shopping.payment.domain.enums.PaymentStates;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
public abstract class Payment extends BaseEntity {

    @Column
    @Range(min = 0)
    private Integer amount;
    @Column
    @NotNull
    private Integer refundedAmount;
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStates state;
    @Enumerated(EnumType.STRING)
    private PaymentStateUpdateSubscriberTypes paymentStateUpdateSubscriberTypes;

    protected Payment(Integer amount) {
        this.amount = amount;
        this.state = PaymentStates.PENDING;
        this.paymentStateUpdateSubscriberTypes = PaymentStateUpdateSubscriberTypes.NONE;
        this.refundedAmount = 0;
    }


    @Override
    public boolean equals(Object obj) {
        Payment target = (Payment) obj;
        return target.getId().equals(getId());
    }

    public Integer getRefundableAmount() {
        return this.getAmount() - this.getRefundedAmount();
    }

    public void setState(PaymentStates state) {
        this.state = state;
    }

    public void setRefundedAmount(int refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public void setSubscriberType(PaymentStateUpdateSubscriberTypes subscriberType) {
        this.paymentStateUpdateSubscriberTypes = subscriberType;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
