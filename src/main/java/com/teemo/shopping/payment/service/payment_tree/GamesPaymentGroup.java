package com.teemo.shopping.payment.service.payment_tree;

import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.payment.domain.Payment;
import com.teemo.shopping.payment.service.factory.OrderContext;

import java.util.List;
import java.util.Optional;

public class GamesPaymentGroup extends PaymentGroup {
    public GamesPaymentGroup(List<Game> games) {
        this.games = games;
    }

    List<Game> games;

    @Override
    protected OrderContext getContext(OrderContext context, Optional<List<Payment>> childPayments) {
        context.setGames(Optional.of(games));
        if(childPayments.isEmpty()) {
            context.setRemainAmount(getGamesAmount());
        } else {
            context.setRemainAmount(getGamesAmount() - getPaymentsAmount(childPayments.get()));
        }
        return context;
    }

    @Override
    protected void throwIf(List<Payment> payments) {
        int gamesAmount = getGamesAmount();
        int paymentsAmount = getPaymentsAmount(payments);
        if(gamesAmount != paymentsAmount) {
            throw new IllegalStateException("결제총액이 주문금액과 일치하지 않음");
        }
    }
    private int getGamesAmount() {
        int amount = 0;
        for(var game : games) {
            amount += game.getPrice();
        }
        return amount;
    }
    private int getPaymentsAmount(List<Payment> payments) {
        int amount = 0;
        for(var payment : payments) {
            amount += payment.getAmount();
        }
        return amount;
    }
}
