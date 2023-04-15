package com.teemo.shopping.order.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsOwnGames;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsOwnGamesRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.repository.CouponRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import com.teemo.shopping.order.domain.AllProductPayment;
import com.teemo.shopping.order.domain.GameProductPayment;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.OrdersGames;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.domain.PointPayment;
import com.teemo.shopping.order.dto.OrderDTO;
import com.teemo.shopping.order.dto.payment.PaymentDTO;
import com.teemo.shopping.order.enums.OrderStatus;
import com.teemo.shopping.order.enums.OrdersGamesStatus;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.enums.PaymentStatus;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.OrdersGamesRepository;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.order.service.factory.CouponPaymentFactory;
import com.teemo.shopping.order.service.factory.DiscountPaymentFactory;
import com.teemo.shopping.order.service.factory.KakaopayPaymentFactory;
import com.teemo.shopping.order.service.factory.PaymentFactory;
import com.teemo.shopping.order.service.factory.PointPaymentFactory;
import com.teemo.shopping.order.service.parameter.PaymentRefundParameter;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.jaxb.SpringDataJaxb.OrderDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private List<PaymentService> paymentServices;
    private List<PaymentFactory> allProductPaymentFactories = new ArrayList<>();
    private List<PaymentFactory> gameProductPaymentFactories = new ArrayList<>();
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrdersGamesRepository ordersGamesRepository;
    @Autowired
    private AccountsOwnGamesRepository accountsOwnGamesRepository;
    @Autowired
    private PaymentRepository<Payment> paymentRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    OrderService(DiscountPaymentFactory discountPaymentFactory,
        CouponPaymentFactory couponPaymentFactory, PointPaymentFactory pointPaymentFactory,
        KakaopayPaymentFactory kakaopayPaymentFactory) {
        // 이 순서대로 결제 실행
        gameProductPaymentFactories.add(discountPaymentFactory);
        gameProductPaymentFactories.add(couponPaymentFactory);
        allProductPaymentFactories.add(pointPaymentFactory);
        allProductPaymentFactories.add(kakaopayPaymentFactory);
    }

    @PostConstruct
    public void init() {

    }

    //Todo: account 가 game을 이미 가지고있으면 Exception 추가
    @Transactional
    public Long addOrder(Long accountId, int point, List<PaymentMethod> methods,
        List<Long> gameIds, Map<Long, Long> gameCouponIdMap, String redirect) {
        Account account = accountRepository.findById(accountId).get();
        List<Long> couponIds = new ArrayList<>();
        for (var gameCouponEntry : gameCouponIdMap.entrySet()) {
            couponIds.add(gameCouponEntry.getValue());
        }
        List<Game> games = gameRepository.findAllById(gameIds);
        List<Coupon> coupons = couponRepository.findAllById(couponIds);

        boolean isAlreadyOwnGame = accountsOwnGamesRepository.findAllByAccount(account).stream()
            .anyMatch(accountsGamesEntry -> accountsGamesEntry.getGame().equals(games));
        if (isAlreadyOwnGame) {
            throw new IllegalStateException("이미 소유하고 있는 게임임.");
        }
        if (games.size() != gameIds.size()) {
            throw new NoSuchElementException("존재 하지 않는 게임임.");
        }
        if (coupons.size() != couponIds.size()) {
            throw new NoSuchElementException("존재 하지 않는 쿠폰임.");
        }

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
        Set<Class<? extends Payment>> availablePaymentClasses = new HashSet<>();
        ;
        for (var method : methods) {
            availablePaymentClasses.add(method.getPaymentClass());
        }

        int totalPrice = games.stream().mapToInt(game -> game.getPrice())
            .reduce(0, (accPrice, v) -> accPrice + v);

        Order order = Order.builder().account(account).totalPrice(totalPrice)
            .status(OrderStatus.PENDING).build();
        order = orderRepository.save(order);

        List<Payment> payments = new ArrayList<>();
        int totalRemainPrice = 0;
        for (var game : games) {    // 게임 개별에 결제 적용
            Optional<Coupon> coupon = gameCouponMap.get(game);
            int gameRemainPrice = game.getPrice();
            for (var gameProductPaymentFactory : gameProductPaymentFactories) {
                if (gameRemainPrice == 0) {    // 남은 금액이 0 이면 끝
                    break;
                }
                OrderCreateContext orderCreateContext = OrderCreateContext.builder()  // Context 생성
                    .game(game).coupon(coupon).account(account).amount(gameRemainPrice).order(order)
                    .redirect(redirect)
                    .build();

                Class nowPaymentClass = gameProductPaymentFactory.getPaymentClass();
                // 선택한 결제 수단이 아니면 PASS
                if (!availablePaymentClasses.contains(nowPaymentClass)) {
                    continue;
                }
                Optional<Payment> paymentOptional;
                try {
                    paymentOptional = gameProductPaymentFactory.create(orderCreateContext);
                } catch (IllegalStateException e) {
                    // improve: PaymentExceptionHandler
                    throw new IllegalStateException(nowPaymentClass.toString() + " 결제 수단 결제 실패");
                }
                // 결제 수단이 생성 되지 않으면 PASS
                if (paymentOptional.isEmpty()) {
                    continue;
                }
                Payment payment = paymentOptional.get();
                gameRemainPrice -= payment.getAmount();
                payments.add(payment);
            }
            totalRemainPrice += gameRemainPrice;
        }

        {   // 게임 전체에 결제 적용
            String itemName = String.join(",", games.stream().map(game -> game.getName()).toList());
            for (var allProductPaymentFactory : allProductPaymentFactories) {
                if (totalRemainPrice == 0) {    // 남은 금액이 0 이면 끝
                    break;
                }
                OrderCreateContext orderCreateContext = OrderCreateContext.builder() // context 생성
                    .games(games).account(account).point(point).order(order).itemName(itemName)
                    .amount(totalRemainPrice).redirect(redirect).build();
                Class nowPaymentClass = allProductPaymentFactory.getPaymentClass();
                // 선택한 결제 수단이 아니면 PASS
                if (!availablePaymentClasses.contains(nowPaymentClass)) {
                    continue;
                }
                Optional<Payment> paymentOptional;
                try {
                    paymentOptional = allProductPaymentFactory.create(orderCreateContext);
                } catch (IllegalStateException e) {
                    throw new IllegalStateException(nowPaymentClass.toString() + " 결제 수단 결제 실패");
                }
                // 결제 수단이 생성 되지 않으면 PASS
                if (paymentOptional.isEmpty()) {
                    continue;
                }
                Payment payment = paymentOptional.get();
                totalRemainPrice -= payment.getAmount();
                payments.add(payment);
            }
        }
        if (totalRemainPrice != 0) {
            throw new IllegalStateException("결제액이 부족합니다.");
        }

        for (var game : games) { // 주문에 결제 연관
            ordersGamesRepository.save(
                OrdersGames.builder().game(game).order(order).status(OrdersGamesStatus.PENDING)
                    .build());
        }
        updateOrder(order.getId());
        return order.getId();
    }

    /**
     * @param orderId
     * @return order 반환
     */
    @Transactional(readOnly = true)
    public OrderDTO get(Long orderId) {
        Order order;
        try {
            order = orderRepository.findById(orderId).get();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("orderId를 가지고있는 Order 없음");
        }
        return OrderDTO.from(order);
    }

    /**
     * @param accountId
     * @return account가 소유하는 orders 반환
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> list(Long accountId) {
        Account account;
        try {
            account = accountRepository.findById(accountId).get();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("accountId를 가지고있는 Account 없음");
        }
        return orderRepository.findAllByAccount(account).stream().map(order -> OrderDTO.from(order))
            .toList();
    }

    /**
     * Payments 에서 Status.Pending이 있으면 Skip Status.CANCEL 이 하나라도 있으면 주문 취소 전부 Status.SUCCESS 면 주문
     * 성공
     */
    @Transactional
    public void updateOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        List<Payment> payments = paymentRepository.findAllByOrder(order);
        List<OrdersGames> ordersGames = ordersGamesRepository.findAll();
        boolean isPaymentPendingRemain = false;
        boolean isPaymentCancelRemain = false;
        for (var payment : payments) {
            if (payment.getStatus().equals(PaymentStatus.CANCEL)) {
                isPaymentCancelRemain = true;
            } else if (payment.getStatus().equals(PaymentStatus.PENDING)) {
                isPaymentPendingRemain = true;
            }
        }
        boolean isOrderSuccess = !isPaymentPendingRemain && !isPaymentCancelRemain;
        boolean isOrderCancel = !isPaymentPendingRemain && isPaymentCancelRemain;

        if (isOrderCancel) {    // 주문 취소
            order.updateStatus(OrderStatus.CANCEL);
            refundPayments(payments);
            for (var ordersGamesEntry : ordersGames) {
                ordersGamesEntry.updateStatus(OrdersGamesStatus.CANCEL);
            }
        }
        if (isOrderSuccess) {       // 주문 성공
            order.updateStatus(OrderStatus.SUCCESS);
            Account account = order.getAccount();
            for (var ordersGamesEntry : ordersGames) {
                ordersGamesEntry.updateStatus(OrdersGamesStatus.PURCHASE);
                Game game = ordersGamesEntry.getGame();
                accountsOwnGamesRepository.save(
                    AccountsOwnGames.builder().account(account).game(game).build());
            }

        }
    }

    /**
     * 입력된 모든 결제를 환불
     */

    @Transactional
    public void refundPayments(Map<Payment, Integer> paymentRefundPriceMap) {
        for (var paymentRefundPriceEntry : paymentRefundPriceMap.entrySet()) {
            Payment payment = paymentRefundPriceEntry.getKey();
            Integer refundPrice = paymentRefundPriceEntry.getValue();
            if (!(payment.getStatus() == PaymentStatus.SUCCESS
                || payment.getStatus() == PaymentStatus.PARTIAL_REFUNDED)) {  // 환불 가능한 상태인지 확인
                continue;
            }
            // 환불 금액이 환불 가능 금액 보다 클때
            if (refundPrice > payment.getRefundableAmount()) {
                throw new RuntimeException();
            }
            for (var paymentService : paymentServices) { //improve: Hashmap 사용해서 O(1) 가능
                if (paymentService.getPaymentClass().equals(payment.getClass())) {
                    paymentService.refund(
                        PaymentRefundParameter.builder().paymentId(payment.getId())
                            .refundPrice(payment.getRefundedAmount() + refundPrice)
                            .build());   // 환불 가능한 금액만 환불
                }
            }
        }

    }

    public void refundPayments(List<Payment> payments) {
        Map<Payment, Integer> paymentRefundPriceMap = new HashMap<>();
        payments.stream().forEach((payment) -> {
            paymentRefundPriceMap.put(payment, payment.getRefundableAmount());
        });
        refundPayments(paymentRefundPriceMap);
    }

    public void refundPayment(Payment payment) {
        refundPayments(List.of(payment));
    }

    public void refundPayment(Payment payment, Integer refundPrice) {
        refundPayments(Map.of(payment, refundPrice));
    }


    /**
     * 주문을 환불하고 게정과 게임 관계 삭제
     */
    @Transactional
    public void refundOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        Account account = order.getAccount();
        List<Payment> payments = paymentRepository.findAllByOrder(order);

        refundPayments(payments);

        List<OrdersGames> ordersGames = ordersGamesRepository.findAllByOrder(order);
        ordersGames.forEach((ordersGamesEntry) -> {
            if (ordersGamesEntry.getStatus().equals(OrdersGamesStatus.PURCHASE)) {
                accountsOwnGamesRepository.findByAccountAndGame(account,
                    ordersGamesEntry.getGame());
                ordersGamesEntry.updateStatus(OrdersGamesStatus.REFUND);
            }
        });
    }

    @Transactional
    public void refundGame(Long orderId, Long gameId) {
        Order order = orderRepository.findById(orderId).get();
        Game game = gameRepository.findById(gameId).get();
        Account account = order.getAccount();

        //주문 상태 환불로 변경
        OrdersGames ordersGames = ordersGamesRepository.findByOrderAndGame(order, game).get();
        ordersGames.updateStatus(OrdersGamesStatus.REFUND);

        //계정에서 게임 제거
        AccountsOwnGames accountsOwnGames = accountsOwnGamesRepository.findByAccountAndGame(account,
            game).get();
        accountsOwnGamesRepository.delete(accountsOwnGames);

        /**
         * 환불 절차
         * 1. gameProductPayment 전부 환불
         * 2. finalPayment 환불
         * 2.1 현금 부터 환불
         * 2.2 환불할 현금이 없으면 포인트 환불
         */
        List<Payment> payments = paymentRepository.findAllByOrder(order);
        List<Payment> gameProductPayments = new ArrayList<>();
        List<Payment> allProductPayments = new ArrayList<>();

        for (var payment : payments) {
            if (payment instanceof GameProductPayment) {
                gameProductPayments.add(payment);
            } else if (payment instanceof AllProductPayment) {
                allProductPayments.add(payment);
            }
        }
        int remainRefundPrice = 0;
        for (var payment : payments) {
            remainRefundPrice += payment.getAmount();
        }
        refundPayments(gameProductPayments);
        for (var gameProductPayment : gameProductPayments) {
            remainRefundPrice -= gameProductPayment.getAmount();
        }

        /** 2. finalPayment 환불
         확장 가능한 형식이지만 너무 코드 길음
         finalPaymentPriorityMap 의 경우 그냥 데이터
         Alternative 2가지
         1. Payment 에 order 내장
         2. Data class 나 Method 만들어서 Mapping
         */

        Map<Class, Integer> allProductPaymentPriorityMap = new HashMap<>();
        allProductPaymentPriorityMap.put(PointPayment.class, Integer.MAX_VALUE); // 가장 우선순위 낮음
        int defaultPriority = Integer.MAX_VALUE / 2;
        Set<AllProductPayment> allProductPaymentSet = new TreeSet<>((o1, o2) -> {
            Integer aPriority = allProductPaymentPriorityMap.get(o1.getClass());
            Integer bPriority = allProductPaymentPriorityMap.get(o2.getClass());
            if (aPriority == null) {
                aPriority = defaultPriority;
            }
            if (bPriority == null) {
                bPriority = defaultPriority;
            }
            return bPriority - aPriority;
        });

        for (var allProductPayment : allProductPayments) {
            allProductPaymentSet.add((AllProductPayment) allProductPayment);
        }

        for (var allProductPayment : allProductPaymentSet) {
            if (remainRefundPrice == 0) {
                break;
            }
            int nowRefundablePrice = Math.min(allProductPayment.getRefundableAmount(),
                remainRefundPrice);
            refundPayment(allProductPayment, nowRefundablePrice);
            remainRefundPrice -= nowRefundablePrice;
        }
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByStatus(Long orderId, PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(orderId).get();
        return paymentRepository.findAllByOrderAndStatus(order, paymentStatus).stream()
            .map(payment -> PaymentDTO.from(payment)).toList();
    }
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPayments(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        return paymentRepository.findAllByOrder(order).stream()
            .map(payment -> PaymentDTO.from(payment)).toList();
    }
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByAccount(Long accountId) {
        Account account = accountRepository.findById(accountId).get();
        List<Order> orders = orderRepository.findAllByAccount(account);
        return orders.stream().map(order -> OrderDTO.from(order)).toList();
    }
}

