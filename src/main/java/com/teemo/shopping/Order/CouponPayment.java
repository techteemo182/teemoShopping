package com.teemo.shopping.Order;

import com.teemo.shopping.Order.PaymentMethod.Values;
import com.teemo.shopping.coupon.Coupon;
import com.teemo.shopping.game.Game;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
    public CouponPayment(PaymentMethod method, double price, String info) {
        super(method, price, info);
    }

    @ManyToOne
    Coupon coupon;

    @ManyToOne
    Game game;
}
