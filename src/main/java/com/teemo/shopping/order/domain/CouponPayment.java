package com.teemo.shopping.order.domain;

import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.enums.PaymentMethod.Values;
import com.teemo.shopping.order.enums.PaymentStatus;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
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
public class CouponPayment extends GameProductPayment {

    @Builder
    public CouponPayment(int amount, PaymentStatus status, Order order, Coupon coupon, Game game) {
        super(amount, status, order, PaymentMethod.COUPON, game);
        this.coupon = coupon;
    }

    @ManyToOne
    @JoinColumn(name = "coupons_id")
    @NotNull
    Coupon coupon;
}
