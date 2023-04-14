package com.teemo.shopping.order.service;

import com.teemo.shopping.order.domain.AllProductPayment;
import com.teemo.shopping.order.domain.CouponPayment;
import com.teemo.shopping.order.domain.DiscountPayment;
import com.teemo.shopping.order.domain.GameProductPayment;
import com.teemo.shopping.order.domain.KakaopayPayment;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.OrdersGames;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.domain.PointPayment;
import com.teemo.shopping.order.dto.payment_create_param.PaymentCreateParamFactory;
import com.teemo.shopping.order.enums.OrderStatus;
import com.teemo.shopping.order.enums.OrdersGamesStatus;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.enums.PaymentStatus;
import com.teemo.shopping.order.dto.OrderCreateContext;
import com.teemo.shopping.order.dto.OrderDTO;
import com.teemo.shopping.order.dto.PaymentRefundParameter;
import com.teemo.shopping.order.dto.payment_create_param.CouponPaymentCreateParam;
import com.teemo.shopping.order.dto.payment_create_param.DiscountPaymentCreateParam;
import com.teemo.shopping.order.dto.payment_create_param.KakaopayPaymentCreateParam;
import com.teemo.shopping.order.dto.payment_create_param.PaymentCreateParam;
import com.teemo.shopping.order.dto.payment_create_param.PointPaymentCreateParam;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.OrdersGamesRepository;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsOwnGames;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsOwnGamesRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.repository.CouponRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private List<PaymentService> paymentServices;
    private List<AllProductPaymentService> allProductPaymentServices = new ArrayList<>();
    private List<GameProductPaymentService> gameProductPaymentServices = new ArrayList<>();
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrdersGamesRepository ordersGamesRepository;
    @Autowired
    private AccountsOwnGamesRepository accountsOwnGamesRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CouponRepository couponRepository;

    private PaymentCreateParamFactory paymentCreateParamFactory = new PaymentCreateParamFactory();
    @Autowired
    OrderService(DiscountPaymentService discountPaymentService,
        CouponPaymentService couponPaymentService, PointPaymentService pointPaymentService,
        KakaopayPaymentService kakaopayPaymentService) {
        // 이 순서대로 결제 실행
        gameProductPaymentServices.add(discountPaymentService);
        gameProductPaymentServices.add(couponPaymentService);
        allProductPaymentServices.add(pointPaymentService);
        allProductPaymentServices.add(kakaopayPaymentService);
    }
    /**
     * 주문을 생성하는 서비스입니다. 다양한 결제 수단을 사용할 수 있습니다. 할인, 쿠폰, 포인트등을 주문에 적용합니다. 가격의 적용은 (할인, 쿠폰, 포인트, 카카오페이)
     * 순으로 진행됩니다. 모든 결제수단은 다른 결제수단들이 완료되어야 PaymentStatus.SUCCESS 가 됩니다. 안료되지 않은 경우
     * PaymentStatus.PENDING 입니다. 카카오페이등 외부 API를 사용하여 구현되는 경우 예) 카카오 페이로 3000원 결제가 필요한 경우 지연이 되므로 외부
     * API 사용으로 지연되므로 Order.status = OrderStatus.PENDING 사용한 모든 Payment.status = OrderStatus.PENDING
     * 이 경우 할인 쿠폰 포인트등 내부 API를 사용하여 구현 되는 경우 바로 처리가 가능하므로 예) 100% 할인으로 남은 결제금액 0원인 경우 Order.status =
     * OrderStatus.SUCCESS 사용한 모든 Payment.status = OrderStatus.SUCCESS
     */
    //Todo: account 가 game을 이미 가지고있으면 Exception 추가
    @Transactional
    public Long createOrder(Long accountId, int point, List<PaymentMethod> methods, List<Long> gameIds,  Map<Long, Long> gameCouponIdMap) {
        Account account = accountRepository.findById(accountId).get();
        List<Long> couponIds = new ArrayList<>();
        for(var gameCouponEntry : gameCouponIdMap.entrySet()) {
            couponIds.add(gameCouponEntry.getValue());
        }
        List<Game> games = gameRepository.findAllById(gameIds);
        List<Coupon> coupons = couponRepository.findAllById(couponIds);

        if(games.size() != gameIds.size()) {
            throw new NoSuchElementException("존재 하지 않는 게임임.");
        }
        if(coupons.size() != couponIds.size()) {
            throw new NoSuchElementException("존재 하지 않는 쿠폰임.");
        }
        
        Map<Long, Game> idGameMap = new HashMap<>();
        Map<Long, Coupon> idCouponMap = new HashMap<>();
        games.forEach((game) -> idGameMap.put(game.getId(), game));
        coupons.forEach((coupon) -> idCouponMap.put(coupon.getId(), coupon));

        Map<Game, Optional<Coupon>> gameCouponMap = new HashMap<>();
        for(var gameId : gameIds) {
            Game game = idGameMap.get(gameId);
            Optional<Coupon> coupon = gameCouponIdMap.containsKey(game) ? Optional.of(idCouponMap.get(gameCouponIdMap.get(game))) : Optional.empty();
            gameCouponMap.put(game,coupon);
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
            for (var gameProductPaymentService : gameProductPaymentServices) {
                if (gameRemainPrice == 0) {    // 남은 금액이 0 이면 끝
                    break;
                }
                OrderCreateContext orderCreateContext = OrderCreateContext.builder()  // Context 생성
                    .game(game).coupon(coupon).account(account)
                    .amount(gameRemainPrice).order(order).build();
                Optional<Long> optionalPaymentId;
                try {
                    optionalPaymentId = gameProductPaymentService.create(
                        paymentCreateParamFactory.create(
                                gameProductPaymentService.getTargetPaymentClass(), orderCreateContext)
                            .get());
                } catch (IllegalStateException e) {
                    // improve: PaymentExceptionHandler
                    throw new IllegalStateException("결제 실패");
                }
                if (optionalPaymentId.isEmpty()) {
                    continue;
                }
                Payment payment = (Payment) paymentRepository.findById(optionalPaymentId.get())
                    .get();
                gameRemainPrice -= payment.getAmount();
                payments.add(payment);
            }
            totalRemainPrice += gameRemainPrice;
        }

        {
            String itemName = String.join(",", games.stream().map(game -> game.getName()).toList());
            for (var allProductPaymentService : allProductPaymentServices) {
                if (totalRemainPrice == 0) {    // 남은 금액이 0 이면 끝
                    break;
                }
                OrderCreateContext orderCreateContext = OrderCreateContext.builder() // context 생성
                    .games(games).account(account).point(point).order(order).itemName(itemName)
                    .amount(totalRemainPrice).build();
                Optional<Long> optionalPaymentId;
                try {
                    optionalPaymentId = allProductPaymentService.create(
                        paymentCreateParamFactory.create(
                                allProductPaymentService.getTargetPaymentClass(), orderCreateContext)
                            .get());
                } catch (IllegalStateException e) {
                    // improve: PaymentExceptionHandler
                    throw new IllegalStateException("결제 실패");
                }
                if (optionalPaymentId.isEmpty()) {
                    continue;
                }
                Payment payment = (Payment) paymentRepository.findById(optionalPaymentId.get())
                    .get();
                totalRemainPrice -= payment.getAmount();
                payments.add(payment);
            }
        }
        if (totalRemainPrice != 0) {
            throw new IllegalStateException("결제 총액이 결제 총액과 맞지 않음");
        }

        for (var game : games) { // 전체 게임에 결제 적용
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
     *
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
        return orderRepository.findAllByAccount(account)
            .stream().map(order -> OrderDTO.from(order)).toList();
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
                if (paymentService.getTargetPaymentClass()
                    .equals(payment.getClass())) {
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
                accountsOwnGamesRepository.findByAccountAndGame(account, ordersGamesEntry.getGame());
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
        AccountsOwnGames accountsOwnGames = accountsOwnGamesRepository.findByAccountAndGame(account, game)
            .get();
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
    List<Long> getPendingPayments(Long orderId) {

    }
}

