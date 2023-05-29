package com.teemo.shopping.order.service;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.dto.request.GamePaymentInformation;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.repository.CouponRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.OrdersGames;
import com.teemo.shopping.payment.domain.KakaopayPayment;
import com.teemo.shopping.payment.domain.Payment;
import com.teemo.shopping.order.domain.enums.OrderStates;
import com.teemo.shopping.order.domain.enums.OrdersGamesStates;
import com.teemo.shopping.payment.domain.enums.PaymentMethods;
import com.teemo.shopping.payment.domain.enums.PaymentStateUpdateSubscriberTypes;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.OrdersGamesRepository;
import com.teemo.shopping.order.service.aspect.CreateOrderTransaction;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.payment.service.CouponPaymentService;
import com.teemo.shopping.payment.service.DiscountPaymentService;
import com.teemo.shopping.payment.service.KakaopayPaymentService;
import com.teemo.shopping.payment.service.PaymentService;
import com.teemo.shopping.payment.service.PointPaymentService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.teemo.shopping.payment.service.factory.*;
import com.teemo.shopping.payment.service.payment_tree.PaymentNode;
import com.teemo.shopping.payment.service.payment_tree.builder.PaymentTreeBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderCreateService {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrdersGamesRepository ordersGamesRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CouponRepository couponRepository;
    @AllArgsConstructor(staticName = "of")
    @Getter
    static public class OrderOption {

        private Optional<Integer> point;
        private Optional<PaymentMethods> paymentMethods;
        private Optional<String> redirect;
    }
    @Transactional
    @CreateOrderTransaction
    public Long createOrder(Long accountId, OrderOption orderOption,
        List<GamePaymentInformation> gamePaymentInfos) throws Exception {
        PreparedData preparedData = prepareData(accountId, gamePaymentInfos);

        Order order = Order.builder().account(preparedData.getAccount())
            .totalPrice(preparedData.getTotalPrice()).state(OrderStates.PENDING).build();

        List<Payment> payments = createPayments(preparedData, orderOption);

        throwIfSufficientPaymentAmount(preparedData.getTotalPrice(), payments);
        order.getPayments().addAll(payments);
        createOrderGames(preparedData, order);
        order = orderRepository.save(order);

        return order.getId();
    }
    protected List<Payment> createPayments(PreparedData preparedData, OrderOption orderOption) {
        PaymentTreeBuilder paymentTreeBuilder = new PaymentTreeBuilder();
        for(var game : preparedData.getGames()) {
            Optional<Coupon> coupon = preparedData.getGameCouponMap().get(game);
            paymentTreeBuilder.addGame(game, coupon);
            paymentTreeBuilder.addGamePayment(game, new DiscountPaymentFactory(), Optional.empty());
            if(coupon.isPresent()) {
                paymentTreeBuilder.addGamePayment(game, new CouponPaymentFactory(), Optional.empty());
            }
        }
        if(orderOption.getPoint().isPresent()) {
            paymentTreeBuilder.addPayment(new PointPaymentFactory(), orderOption.getPoint());
        }
        if(orderOption.getPaymentMethods().isPresent()) {
            PaymentMethods paymentMethod = orderOption.getPaymentMethods().get();
            if(paymentMethod.equals(PaymentMethods.KAKAOPAY)) {
                paymentTreeBuilder.addPayment(new KakaopayPaymentFactory(), Optional.empty());
            }
        }
        PaymentNode paymentNode = paymentTreeBuilder.build();
        return paymentNode.create(OrderContext.builder().build());
    }


    protected PreparedData prepareData(Long accountId,          // 데이터 준비하는 작업은 서비스에서 해야함
        List<GamePaymentInformation> gamePaymentInfos) throws Exception {
        Account account = accountRepository.findById(accountId).get();
        List<Long> gameIds = new ArrayList<>();
        List<Long> couponIds = new ArrayList<>();
        Map<Long, Long> gameCouponIdMap = new HashMap<>();
        for (var gamePaymentInfo : gamePaymentInfos) {
            gameIds.add(gamePaymentInfo.getGameId());
            if (gamePaymentInfo.getCouponId().isPresent()) {
                gameCouponIdMap.put(gamePaymentInfo.getGameId(),
                    gamePaymentInfo.getCouponId().get());
                couponIds.add(gamePaymentInfo.getCouponId().get());
            }
        }
        List<Game> games = gameRepository.findAllById(gameIds);
        List<Coupon> coupons = couponRepository.findAllById(couponIds);
        Map<Game, Optional<Coupon>> gameCouponMap = createGameCouponMap(games, coupons, gameIds,
            gameCouponIdMap);
        throwIfCantCreate(accountId, gameIds, games, coupons, couponIds.size());
        int totalPrice = games.stream().mapToInt(game -> game.getPrice())
            .reduce(0, (accPrice, v) -> accPrice + v);
        return PreparedData.of(account, games, gameCouponMap, totalPrice);
    }
    @AllArgsConstructor(staticName = "of")
    @Getter
    static public class PreparedData {

        private Account account;
        private List<Game> games;
        private Map<Game, Optional<Coupon>> gameCouponMap;
        private int totalPrice;
    }
    protected void throwIfCantCreate(Long accountId, List<Long> gameIds, List<Game> games,
                                     List<Coupon> coupons, int neededCouponCount) throws Exception {
        boolean isPurchasable = ordersGamesRepository.isPurchasable(accountId, gameIds);
        if (isPurchasable) {
            throw new IllegalStateException("구매 가능한 상태가 아닙니다.");
        }
        int neededGameCount = gameIds.size();
        if (games.size() != neededGameCount) {
            throw new NoSuchElementException("존재 하지 않는 게임임.");
        }
        if (coupons.size() != neededCouponCount) {
            throw new NoSuchElementException("존재 하지 않는 쿠폰임.");
        }
    }
    protected Map<Game, Optional<Coupon>> createGameCouponMap(List<Game> games,
                                                              List<Coupon> coupons, List<Long> gameIds, Map<Long, Long> gameCouponIdMap) {
        Map<Long, Game> idGameMap = new HashMap<>();
        Map<Long, Coupon> idCouponMap = new HashMap<>();
        games.forEach((game) -> idGameMap.put(game.getId(), game));
        coupons.forEach((coupon) -> idCouponMap.put(coupon.getId(), coupon));
        Map<Game, Optional<Coupon>> gameCouponMap = new HashMap<>();
        for (var gameId : gameIds) {
            Game game = idGameMap.get(gameId);
            Optional<Coupon> coupon = gameCouponIdMap.containsKey(game) ? Optional.of(
                    idCouponMap.get(gameCouponIdMap.get(game))) : Optional.empty();
            gameCouponMap.put(game, coupon);
        }
        return gameCouponMap;
    }

    protected void throwIfSufficientPaymentAmount(int totalPrice, List<Payment> payments)
        throws Exception {
        int remainPrice = totalPrice;
        for (var payment : payments) {
            remainPrice -= payment.getAmount();
        }
        if (remainPrice != 0) {
            throw new IllegalStateException("결제액이 부족합니다.");
        }
    }

    protected List<OrdersGames> createOrderGames(PreparedData preparedData, Order order) {
        List<OrdersGames> ordersGamesList = new ArrayList<>();
        for (var game : preparedData.getGames()) {
            OrdersGames ordersGames = OrdersGames.builder().game(game).order(order)
                .account(preparedData.getAccount()).price(game.getPrice())
                .state(OrdersGamesStates.PENDING).build();
            order.getOrdersGames().add(ordersGames);
            ordersGamesList.add(ordersGames);
        }
        return ordersGamesList;
    }



}
