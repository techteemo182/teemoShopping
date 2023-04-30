package com.teemo.shopping.order.domain;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.order.enums.PaymentMethods;
import com.teemo.shopping.order.enums.PaymentMethods.Values;
import com.teemo.shopping.order.enums.PaymentStates;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import jakarta.persistence.Column;
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

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table (
    name = "coupon_payments"
)
@Getter
@DiscriminatorValue(Values.COUPON)
public class CouponPayment extends Payment {
    @Builder
    public CouponPayment(Integer amount, PaymentStates state, Order order, PaymentMethods method,
        Coupon coupon, Game game, Account account) {
        super(amount, state, order, PaymentMethods.COUPON);
        this.coupon = coupon;
        this.game = game;
        this.account = account;
    }

    @ManyToOne
    @JoinColumn(name = "coupons_id")
    @NotNull
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @NotNull
    private Account account;

    @ManyToOne
    @JoinColumn(name = "games_id")
    @NotNull
    private Game game;


}
