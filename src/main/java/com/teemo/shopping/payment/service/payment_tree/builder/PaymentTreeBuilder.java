package com.teemo.shopping.payment.service.payment_tree.builder;

import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.payment.service.factory.OrderContext;
import com.teemo.shopping.payment.service.factory.PaymentFactory;
import com.teemo.shopping.payment.service.payment_tree.GamePaymentGroup;
import com.teemo.shopping.payment.service.payment_tree.GamesPaymentGroup;
import com.teemo.shopping.payment.service.payment_tree.PaymentLeaf;
import com.teemo.shopping.payment.service.payment_tree.PaymentNode;

import java.util.*;

public class PaymentTreeBuilder {       // improve: 순서를 정하는 방법?
    private List<Game> games = new ArrayList<>();
    private Map<Game, Coupon> gameCouponMap = new HashMap<>();
    private Map<Game, List<PaymentNode>> gamePaymentNodesMap = new HashMap<>();
    private List<PaymentNode> gamesPaymentNodes = new ArrayList<>();

    public void addPayment(PaymentFactory<OrderContext> paymentFactory, Optional<Integer> amount) {
        gamesPaymentNodes.add(new PaymentLeaf(paymentFactory, amount));
    }
    public void addGamePayment(Game game, PaymentFactory<OrderContext> paymentFactory, Optional<Integer> amount) {
        if(!gamePaymentNodesMap.containsKey(game)) {
            gamePaymentNodesMap.put(game, new ArrayList<>());
        }
        List<PaymentNode> paymentNodes = gamePaymentNodesMap.get(game);
        paymentNodes.add(new PaymentLeaf(paymentFactory, amount));
    }
    public void addGame(Game game, Optional<Coupon> coupon) {
        games.add(game);
        if(coupon.isPresent()) {
            gameCouponMap.put(game, coupon.get());
        }
    }
    public PaymentNode build() {
        GamesPaymentGroup gamesPaymentGroup = new GamesPaymentGroup(games);
        for(var game : games) {
            if(!gamePaymentNodesMap.containsKey(game)) {
                continue;
            }
            List<PaymentNode> gamePaymentNodes = gamePaymentNodesMap.get(game);
            Optional<Coupon> coupon = Optional.empty();
            if(gameCouponMap.containsKey(game)) {
                coupon = Optional.of(gameCouponMap.get(game));
            }
            GamePaymentGroup gamePaymentGroup = new GamePaymentGroup(game, coupon);

            for(var gamePaymentNode : gamePaymentNodes) {
                gamePaymentGroup.addPaymentNode(gamePaymentNode);
            }
            gamesPaymentGroup.addPaymentNode(gamePaymentGroup);
        }
        for(var gamesPaymentNode : gamesPaymentNodes) {
            gamesPaymentGroup.addPaymentNode(gamesPaymentNode);
        }

        return gamesPaymentGroup;
    }
}
