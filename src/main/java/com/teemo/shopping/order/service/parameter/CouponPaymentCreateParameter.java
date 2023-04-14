package com.teemo.shopping.order.service.parameter;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.order.domain.Order;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
public class CouponPaymentCreateParameter extends PaymentCreateParameter {

    @Builder
    protected CouponPaymentCreateParameter(
        Account account, Order order, Integer amount, Optional<Coupon> coupon,
        Game game) {
        super(account, order, amount);
        this.coupon = coupon;
        this.game = game;
    }

    private final Optional<Coupon> coupon;
    private final Game game;
}
