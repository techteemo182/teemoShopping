package com.teemo.shopping.payment.service.payment_tree;

import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.payment.domain.Payment;
import com.teemo.shopping.payment.service.factory.OrderContext;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class GamePaymentGroup extends PaymentGroup {
    private Game game;
    private Optional<Coupon> coupon;


    public GamePaymentGroup(Game game, Optional<Coupon> coupon) {
        this.game = game;
        this.coupon = coupon;
    }

    private int getPaymentsAmount(List<Payment> payments) {
        int amount = 0;
        for(var payment : payments) {
            amount += payment.getAmount();
        }
        return amount;
    }

    @Override
    protected OrderContext getContext(OrderContext context, Optional<List<Payment>> childPayments) {
        context.setGame(Optional.of(game));
        context.setCoupon(coupon);
        if(childPayments.isEmpty()) {
            context.setRemainAmount(game.getPrice());
        } else {
            context.setRemainAmount(context.getRemainAmount() - getPaymentsAmount(childPayments.get()));
        }
        return context;
    }

    @Override
    protected void throwIf(List<Payment> payments) {

    }
}
