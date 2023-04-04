package com.teemo.shopping.Order.domain;

import com.teemo.shopping.Order.domain.enums.PaymentMethod.Values;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table (
    name = "coupon_payments"
)
@DiscriminatorValue(Values.COUPON)
public class CouponPayment extends Payment {

    @Builder
    public CouponPayment(int price, PaymentStatus status, Order order, Coupon coupon, Game game) {
        super(price, status, order);
        this.coupon = coupon;
        this.game = game;
    }

    @ManyToOne
    @JoinColumn(name = "coupons_id")
    Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "games_id")
    Game game;
}
