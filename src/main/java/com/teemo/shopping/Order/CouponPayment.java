package com.teemo.shopping.Order;

import com.teemo.shopping.Order.PaymentMethod.Values;
import com.teemo.shopping.coupon.Coupon;
import com.teemo.shopping.game.Game;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Entity
@Table (
    name = "coupon_payments"
)
@DiscriminatorValue(Values.COUPON)
public class CouponPayment extends Payment {

    @Builder
    protected CouponPayment(double price, String info, PaymentStatus status, Coupon coupon,
        Game game) {
        super(price, status);
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
