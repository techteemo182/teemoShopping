package com.teemo.shopping.order.service;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.dto.request.GamePaymentInformation;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsOwnGamesRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.repository.CouponRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.OrdersGames;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.dto.OrderDTO;
import com.teemo.shopping.order.dto.payment.PaymentDTO;
import com.teemo.shopping.order.enums.OrderStates;
import com.teemo.shopping.order.enums.OrdersGamesStates;
import com.teemo.shopping.order.enums.PaymentMethods;
import com.teemo.shopping.order.enums.PaymentStates;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.OrdersGamesRepository;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.order.service.payment.CouponPaymentService;
import com.teemo.shopping.order.service.payment.DiscountPaymentService;
import com.teemo.shopping.order.service.payment.PaymentService;
import com.teemo.shopping.order.service.payment.PointPaymentService;
import com.teemo.shopping.order.service.payment.kakaopay_service.KakaopayPaymentService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OrderService {

    @Autowired
    private List<PaymentService> paymentServices;
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
    private CouponPaymentService couponPaymentService;
    @Autowired
    private DiscountPaymentService discountPaymentService;
    @Autowired
    private KakaopayPaymentService kakaopayPaymentService;
    @Autowired
    private PointPaymentService pointPaymentService;

    @Transactional(rollbackFor = Exception.class)
    public Long create(Long accountId, int point, PaymentMethods paymentMethod,
        List<GamePaymentInformation> gamePaymentInfos, Optional<String> redirect) {
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

        boolean isPurchasable = ordersGamesRepository.isPurchasable(accountId,
            gameIds);        // 돟시 접근시 해결 방법 없음 (Repeatable Read)
        if (isPurchasable) {
            throw new IllegalStateException("구매 가능한 상태가 아닙니다.");
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
        int totalPrice = games.stream().mapToInt(game -> game.getPrice())
            .reduce(0, (accPrice, v) -> accPrice + v);

        // 여기 까지가 ORDER를 생성하기 위한 데이터 전처리

        Order order = Order.builder().account(account).totalPrice(totalPrice)
            .state(OrderStates.PENDING).build(); // order 를 생성 만 하고 저장은 하지 아니함
        // 여기까지가 단순 ORDER 추가
        List<Payment> payments = new ArrayList<>();
        int totalRemainPrice = 0;
        for (var game : games) {    // 게임 개별에 결제 적용
            ArrayList<PaymentService> gamePaymentServices = new ArrayList<>();
            if (game.getDiscountPercent() != 0) {
                gamePaymentServices.add(discountPaymentService);
            }
            if (gameCouponMap.get(game).isPresent()) {
                gamePaymentServices.add(couponPaymentService);
            }
            Optional<Coupon> coupon = gameCouponMap.get(game);
            int gameRemainPrice = game.getPrice();
            for (var gamePaymentService : gamePaymentServices) {
                if (gameRemainPrice == 0) {    // 남은 금액이 0 이면 끝
                    break;
                }
                OrderCreateContext orderCreateContext = OrderCreateContext.builder()  // Context 생성
                    .game(Optional.of(game)).coupon(coupon).account(account).amount(gameRemainPrice)
                    .order(order).redirect(redirect).build();
                Payment payment;
                try {
                    payment = gamePaymentService.create(orderCreateContext);
                } catch (Exception e) {
                    throw new IllegalStateException("결제 수단 셍성 실패");
                }
                gameRemainPrice -= payment.getAmount();
                order.getPayments().add(payment);
            }
            totalRemainPrice += gameRemainPrice;
        }

        {   // 게임 전체에 결제 적용
            ArrayList<PaymentService> commonPaymentServices = new ArrayList<>();
            if (point != 0) {
                commonPaymentServices.add(pointPaymentService);
            }
            if (paymentMethod.equals(PaymentMethods.KAKAOPAY)) {
                commonPaymentServices.add(kakaopayPaymentService);
            }
            for (var commonPaymentService : commonPaymentServices) {
                if (totalRemainPrice == 0) {    // 남은 금액이 0 이면 끝
                    break;
                }
                OrderCreateContext orderCreateContext = OrderCreateContext.builder() // context 생성
                    .games(games).account(account).point(point).order(order)
                    .amount(totalRemainPrice).redirect(redirect).build();
                Payment payment;
                try {
                    payment = commonPaymentService.create(orderCreateContext);
                } catch (Exception e) {
                    throw new IllegalStateException("결제 수단 생성 실패");
                }
                totalRemainPrice -= payment.getAmount();
                order.getPayments().add(payment);
            }
        }
        if (totalRemainPrice != 0) {
            throw new IllegalStateException("결제액이 부족합니다.");
        }
        order.getPayments().addAll(payments);
        for (var game : games) { // 주문에 결제 연관
            OrdersGames ordersGames = OrdersGames.builder().game(game).order(order).account(account)
                .price(game.getPrice()).state(OrdersGamesStates.PENDING).build();
            order.getOrdersGames().add(ordersGames);
        }
        order = orderRepository.save(order);
        return order.getId();
    }

    @Transactional
    public void pay(List<Long> paymentIds) {
        List<Payment> payments = paymentRepository.findAllById(paymentIds);
        for (var paymentService : paymentServices) {
            for (var payment : payments) {
                if (paymentService.getPaymentClass().equals(payment.getClass())) {
                    paymentService.pay(payment.getId());
                }
            }
        }
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


    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByState(Long orderId, PaymentStates paymentStates) {
        Order order = orderRepository.findById(orderId).get();
        return paymentRepository.findAllByOrderAndState(order, paymentStates).stream()
            .map(payment -> PaymentDTO.from(payment)).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPayments(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        return paymentRepository.findAllByOrder(order).stream()
            .map(payment -> PaymentDTO.from(payment)).toList();
    }
}

