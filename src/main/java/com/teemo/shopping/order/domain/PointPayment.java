package com.teemo.shopping.order.domain;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.order.enums.PaymentMethods;
import com.teemo.shopping.order.enums.PaymentMethods.Values;
import com.teemo.shopping.order.enums.PaymentStates;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "point_payments"
)
@DiscriminatorValue(Values.POINT)
public class PointPayment extends Payment {


    @ManyToOne
    @JoinColumn(name = "accounts_id")
    @NotNull
    private Account account;

    @Builder
    public PointPayment(Integer amount,
        Account account) {
        super(amount, PaymentMethods.POINT);
        this.account = account;
    }

}
