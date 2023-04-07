package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.Order;
import com.teemo.shopping.Order.domain.OrdersGames;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.OrderStatus;
import com.teemo.shopping.Order.domain.enums.OrdersGamesStatus;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.CreateOrderReturn;
import com.teemo.shopping.Order.dto.OrderDTO;
import com.teemo.shopping.Order.dto.PaymentCreateContext;
import com.teemo.shopping.Order.dto.PaymentRefundParameter;
import com.teemo.shopping.Order.repository.OrderRepository;
import com.teemo.shopping.Order.repository.OrdersGamesRepository;
import com.teemo.shopping.Order.repository.PaymentRepository;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsGames;
import com.teemo.shopping.account.repository.AccountsGamesRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.game.domain.Game;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrdersGamesRepository ordersGamesRepository;
    @Autowired
    @Qualifier("forOneProduct")
    private List<PaymentService> oneProductPaymentServices;
    @Autowired
    @Qualifier("forAllProduct")
    private List<PaymentService> allProductPaymentServices;

    @Autowired
    private List<PaymentService> paymentServices;
    @Autowired
    private AccountsGamesRepository accountsGamesRepository;

    /**
     * 주문을 생성하는 서비스입니다. 다양한 결제 수단을 사용할 수 있습니다. 할인, 쿠폰, 포인트등을 주문에 적용합니다. 가격의 적용은 (할인, 쿠폰, 포인트, 카카오페이)
     * 순으로 진행됩니다. 모든 결제수단은 다른 결제수단들이 완료되어야 PaymentStatus.SUCCESS 가 됩니다. 안료되지 않은 경우
     * PaymentStatus.PENDING 입니다. 카카오페이등 외부 API를 사용하여 구현되는 경우 예) 카카오 페이로 3000원 결제가 필요한 경우 지연이 되므로 외부
     * API 사용으로 지연되므로 Order.status = OrderStatus.PENDING 사용한 모든 Payment.status = OrderStatus.PENDING
     * 이 경우 할인 쿠폰 포인트등 내부 API를 사용하여 구현 되는 경우 바로 처리가 가능하므로 예) 100% 할인으로 남은 결제금액 0원인 경우 Order.status =
     * OrderStatus.SUCCESS 사용한 모든 Payment.status = OrderStatus.SUCCESS
     */
    @Transactional(rollbackOn = RuntimeException.class)
    public CreateOrderReturn createOrder(@Valid Account account,    //TODO: DTO 로 변경 예정
        Map<@Valid Game, @Valid Optional<Coupon>> gameCouponMap, SortedSet<PaymentMethod> methods,
        int point) {
        var createOrderReturnBuilder = CreateOrderReturn.builder();

        int totalPrice = gameCouponMap.entrySet().stream()
            .mapToInt(entry -> entry.getKey().getPrice()).reduce(0, (accPrice, v) -> accPrice + v);
        createOrderReturnBuilder.totalPrice(totalPrice);

        List<Game> games = gameCouponMap.entrySet().stream().map(entry -> entry.getKey()).toList();

        Order order = Order.builder().account(account).totalPrice(totalPrice)
            .status(OrderStatus.PENDING).build();
        createOrderReturnBuilder.order(OrderDTO.from(order));
        orderRepository.save(order);

        List<Payment> payments = new ArrayList<>();

        int totalRemainPrice = 0;
        for (var game : games) {    // 게임 개별에 결제 적용
            Optional<Coupon> coupon = gameCouponMap.get(game);
            PaymentCreateContext paymentCreateContext = PaymentCreateContext.builder()  // Context 생성
                .createOrderReturnBuilder(createOrderReturnBuilder).game(game)
                .coupon(coupon == null ? Optional.empty() : coupon).account(account)
                .remainPrice(game.getPrice()).order(order).build();
            for (var oneProductPaymentService : oneProductPaymentServices) {
                if (methods.contains(
                    oneProductPaymentService.getPaymentMethod())) { // Payment 생성 해야하는 지 여부 확인
                    oneProductPaymentService.create(paymentCreateContext)
                        .ifPresent(payment -> payments.add(payment));
                }
            }
            totalRemainPrice += paymentCreateContext.getRemainPrice();
        }

        {
            PaymentCreateContext paymentCreateContext = PaymentCreateContext.builder() // context 생성
                .games(games).account(account).point(point).order(order)
                .createOrderReturnBuilder(createOrderReturnBuilder).remainPrice(totalRemainPrice)
                .build();
            for (var paymentService : allProductPaymentServices) {
                if (methods.contains(
                    paymentService.getPaymentMethod())) { // Payment 생성 해야하는 지 여부 확인
                    paymentService.create(paymentCreateContext)
                        .ifPresent(payment -> payments.add(payment));
                }
            }
            totalRemainPrice = paymentCreateContext.getRemainPrice();
        }

        if (totalRemainPrice != 0) {
            throw new RuntimeException();   //ROLLBACk
        }

        order = orderRepository.findById(order.getId()).get();
        for (var game : games) { // 전체 게임에 결제 적용
            ordersGamesRepository.save(
                OrdersGames.builder().game(game).order(order).status(OrdersGamesStatus.PENDING)
                    .build());
        }

        int pointPrice = 0;  // 포인트 사용량
        int discountPrice = 0;    // 할인된 가격
        int couponPrice = 0;    // 쿠폰 가격
        for (var payment : payments) {
            if (payment.getMethod().equals(PaymentMethod.DISCOUNT)) {
                discountPrice += payment.getPrice();
            } else if (payment.getMethod().equals(PaymentMethod.COUPON)) {
                couponPrice += payment.getPrice();
            } else if (payment.getMethod().equals(PaymentMethod.POINT)) {
                pointPrice += payment.getPrice();
            }
        }
        createOrderReturnBuilder.pointPrice(pointPrice);
        createOrderReturnBuilder.couponPrice(couponPrice);
        createOrderReturnBuilder.discountPrice(discountPrice);

        createOrderReturnBuilder.order(updateOrder(order.getId())); // order 업데이트
        return createOrderReturnBuilder.build();
    }

    /**
     *  Payments 에서 Status.Pending이 있으면 Skip
     *  Status.CANCEL 이 하나라도 있으면 주문 취소
     *  전부 Status.SUCCESS 면 주문 성공
     */


    @Transactional
    public OrderDTO updateOrder(Long orderId) throws RuntimeException {
        Order order = orderRepository.findById(orderId).orElseThrow();
        boolean isPaymentPendingRemain = false;
        boolean isPaymentCancelRemain = false;
        for (var payment : order.getPayments()) {
            if (payment.getStatus().equals(PaymentStatus.CANCEL)) {
                isPaymentCancelRemain = true;
            } else if (!payment.getStatus().equals(PaymentStatus.PENDING)) {
                isPaymentPendingRemain = true;
            }
        }
        boolean isOrderSuccess = !isPaymentPendingRemain && !isPaymentCancelRemain;
        boolean isOrderCancel = !isPaymentPendingRemain && isPaymentCancelRemain;



        if (isOrderCancel) {    // 주문 취소
            order.updateStatus(OrderStatus.CANCEL);
            refundPayments(order.getPayments());
            for (var ordersGames : order.getOrdersGames()) {
                ordersGames.updateStatus(OrdersGamesStatus.CANCEL);
            }
        }
        if (isOrderSuccess) {       // 주문 성공
            order.updateStatus(OrderStatus.SUCCESS);
            List<OrdersGames> ordersGames = order.getOrdersGames();
            Account account = order.getAccount();
            for (var ordersGamesEntry : ordersGames) {
                ordersGamesEntry.updateStatus(OrdersGamesStatus.PURCHASE);
                Game game = ordersGamesEntry.getGame();
                accountsGamesRepository.save(
                    AccountsGames.builder().account(account).game(game).build());
            }

        }
        return OrderDTO.from(order);
    }

    /**
     *  입력된 모든 결제를 환불
     */
    @Transactional
    public void refundPayments(List<Payment> payments) {
        for (var payment : payments) {       //주문 전체 취소
            if (!(payment.getStatus() == PaymentStatus.SUCCESS || payment.getStatus() == PaymentStatus.PARTIAL_REFUNDED)) {  // 환불 가능한 상태인지 확인
                continue;
            }
            for (var paymentService : paymentServices) {
                if (paymentService.getPaymentMethod() == payment.getMethod()) {     //improve: Hashmap 사용해서 O(1) 가능
                    paymentService.refund(
                        PaymentRefundParameter.builder().paymentId(payment.getId())
                            .price(payment.getPrice() - payment.getRefundedPrice()).build());   // 환불 가능한 금액만 환불
                }
            }
        }
    }

    /**
     *  주문을 환불하고
     *  게정과 게임 관계 삭제
     */
    @Transactional
    public void refundOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        Account account = order.getAccount();
        List<Payment> payments = order.getPayments();

        refundPayments(payments);

        List<OrdersGames> ordersGames = order.getOrdersGames();
        ordersGames.forEach((ordersGamesEntry) -> {
            if (ordersGamesEntry.getStatus().equals(OrdersGamesStatus.PURCHASE)) {
                accountsGamesRepository.findByAccountAndGame(account, ordersGamesEntry.getGame());
                ordersGamesEntry.updateStatus(OrdersGamesStatus.REFUND);
            }
        });
    }
}

