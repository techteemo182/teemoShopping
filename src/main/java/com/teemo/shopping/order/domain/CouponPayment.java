package com.teemo.shopping.order.domain;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
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

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "coupon_payments"
)
@Getter
@DiscriminatorValue(Values.COUPON)
public class CouponPayment extends Payment {

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

    @Builder
    public CouponPayment(Integer amount,
        Coupon coupon, Game game, Account account) {
        super(amount, PaymentMethods.COUPON);
        this.coupon = coupon;
        this.game = game;
        this.account = account;
    }


}
