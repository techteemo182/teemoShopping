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
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.enums.OrderStates;
import com.teemo.shopping.order.enums.OrdersGamesStates;
import com.teemo.shopping.order.enums.PaymentMethods;
import com.teemo.shopping.order.enums.PaymentStateUpdateSubscriberTypes;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.OrdersGamesRepository;
import com.teemo.shopping.order.service.aspect.CreateOrderTransaction;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.order.service.payment.CouponPaymentService;
import com.teemo.shopping.order.service.payment.DiscountPaymentService;
import com.teemo.shopping.order.service.payment.KakaopayPaymentService;
import com.teemo.shopping.order.service.payment.PaymentService;
import com.teemo.shopping.order.service.payment.PointPaymentService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
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
    @Autowired
    private CouponPaymentService couponPaymentService;
    @Autowired
    private DiscountPaymentService discountPaymentService;
    @Autowired
    private KakaopayPaymentService kakaopayPaymentService;
    @Autowired
    private PointPaymentService pointPaymentService;

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

    protected PreparedData prepareData(Long accountId,
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

    protected List<PaymentService> getApplicableGamePaymentServices(Game game,
        Map<Game, Optional<Coupon>> gameCouponMap) {
        ArrayList<PaymentService> applicableGamePaymentServices = new ArrayList<>();
        if (game.getDiscountPercent() != 0) {
            applicableGamePaymentServices.add(discountPaymentService);
        }
        if (gameCouponMap.get(game).isPresent()) {
            applicableGamePaymentServices.add(couponPaymentService);
        }
        return applicableGamePaymentServices;
    }

    protected List<PaymentService> getApplicableRemainPaymentServices(OrderOption orderOption) {
        ArrayList<PaymentService> applicableRemainPaymentServices = new ArrayList<>();
        if (orderOption.getPoint().isPresent() && orderOption.getPoint().get() != 0) {
            applicableRemainPaymentServices.add(pointPaymentService);
        }
        if (orderOption.getPaymentMethods().isPresent() && orderOption.getPaymentMethods().get().equals(PaymentMethods.KAKAOPAY)) {
            applicableRemainPaymentServices.add(kakaopayPaymentService);
        }
        return applicableRemainPaymentServices;
    }

    protected int calculateRemainPrice(int totalPrice, List<Payment> payments) {
        for (var payment : payments) {
            totalPrice -= payment.getAmount();
        }
        return totalPrice;
    }

    protected List<Payment> createPayments(PreparedData preparedData, OrderOption orderOption) {
        List<Payment> payments = new ArrayList<>();
        List<Payment> gamePayments = createGamePayments(preparedData, orderOption);
        payments.addAll(gamePayments);
        int remainPrice = calculateRemainPrice(preparedData.getTotalPrice(), payments);
        List<Payment> remainPayments = createRemainPayments(preparedData, orderOption, remainPrice);
        payments.addAll(remainPayments);
        payments.forEach((payment) -> payment.setSubscriberType(PaymentStateUpdateSubscriberTypes.ORDER));
        return payments;
    }

    protected List<Payment> createRemainPayments(PreparedData preparedData, OrderOption orderOption,
        int remainPrice) {
        List<Payment> payments = new ArrayList<>();
        List<PaymentService> applicableRemainPaymentServices = getApplicableRemainPaymentServices(
            orderOption);
        for (var applicableRemainPaymentService : applicableRemainPaymentServices) {
            if (remainPrice == 0) {
                break;
            }
            OrderCreateContext orderCreateContext = OrderCreateContext.builder()
                .orderOption(orderOption).preparedData(preparedData).amount(remainPrice).build();
            Payment payment = createPayment(applicableRemainPaymentService, orderCreateContext);
            remainPrice -= payment.getAmount();
            payments.add(payment);
        }
        return payments;
    }

    protected List<Payment> createGamePayments(PreparedData preparedData, OrderOption orderOption) {
        List<Payment> payments = new ArrayList<>();
        for (var game : preparedData.getGames()) {
            List<PaymentService> applicableGamePaymentServices = getApplicableGamePaymentServices(
                game, preparedData.getGameCouponMap());
            int gameRemainPrice = game.getPrice();
            for (var applicableGamePaymentService : applicableGamePaymentServices) {
                if (gameRemainPrice == 0) {
                    break;
                }
                OrderCreateContext orderCreateContext = OrderCreateContext.builder()
                    .orderOption(orderOption).preparedData(preparedData).game(Optional.of(game))
                    .amount(gameRemainPrice).build();
                Payment payment = createPayment(applicableGamePaymentService, orderCreateContext);
                gameRemainPrice -= payment.getAmount();
            }
        }
        return payments;
    }

    protected Payment createPayment(PaymentService paymentService,
        OrderCreateContext orderCreateContext) {
        Payment payment;
        try {
            payment = paymentService.create(orderCreateContext);
        } catch (Exception e) {
            throw new IllegalStateException("결제 수단 생성 실패");
        }
        return payment;
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


    @AllArgsConstructor(staticName = "of")
    @Getter
    static public class OrderOption {

        private Optional<Integer> point;
        private Optional<PaymentMethods> paymentMethods;
        private Optional<String> redirect;
    }

    @AllArgsConstructor(staticName = "of")
    @Getter
    static public class PreparedData {

        private Account account;
        private List<Game> games;
        private Map<Game, Optional<Coupon>> gameCouponMap;
        private int totalPrice;
    }
}
