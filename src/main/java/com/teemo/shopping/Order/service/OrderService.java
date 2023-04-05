package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.Order;
import com.teemo.shopping.Order.domain.OrdersGames;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.OrderStatus;
import com.teemo.shopping.Order.domain.enums.OrdersGamesStatus;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.domain.observer.OrderUpdateObserver;
import com.teemo.shopping.Order.dto.AllGamePaymentServiceContext;
import com.teemo.shopping.Order.dto.CreateOrderReturn;
import com.teemo.shopping.Order.dto.OneGamePaymentServiceContext;
import com.teemo.shopping.Order.dto.OrderDTO;
import com.teemo.shopping.Order.repository.OrderRepository;
import com.teemo.shopping.Order.repository.OrdersGamesRepository;
import com.teemo.shopping.account.domain.Account;
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
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrdersGamesRepository ordersGamesRepository;
    @Autowired
    private List<OneGamePaymentService> oneGamePaymentFactories;
    @Autowired
    private List<AllGamePaymentService> allGamePaymentFactories;


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
            .mapToInt(entry -> entry.getKey().getPrice()).reduce(0, (ingPrice, v) -> ingPrice + v);
        createOrderReturnBuilder.totalPrice(totalPrice);

        List<Game> games = gameCouponMap.entrySet().stream().map(entry -> entry.getKey()).toList();

        Order order = Order.builder().account(account).totalPrice(totalPrice)
            .status(OrderStatus.PENDING).build();
        createOrderReturnBuilder.order(OrderDTO.from(order));
        orderRepository.save(order);
        List<Payment> payments = new ArrayList<>();

        int totalRemainPrice = 0;
        for (var game : games) {
            Optional<Coupon> coupon = gameCouponMap.get(game);
            OneGamePaymentServiceContext oneGamePaymentServiceContext = OneGamePaymentServiceContext.builder()  // Context 생성
                .createOrderReturnBuilder(createOrderReturnBuilder).game(game).coupon(coupon == null ? Optional.empty() : coupon).account(account)
                .remainPrice(game.getPrice()).order(order).build();
            for (var oneGamePaymentFactory : oneGamePaymentFactories) {
                if (methods.contains(
                    oneGamePaymentFactory.getTargetPaymentMethod())) { // Payment 생성 해야하는 지 여부 확인
                    oneGamePaymentFactory.create(oneGamePaymentServiceContext)
                        .ifPresent(payment -> payments.add(payment));
                }
            }
            totalRemainPrice += oneGamePaymentServiceContext.getRemainPrice();
        }


        {
            AllGamePaymentServiceContext allGameProductContext = AllGamePaymentServiceContext.builder() // context 생성
                .games(games).account(account).point(point).order(order).createOrderReturnBuilder(createOrderReturnBuilder).remainPrice(totalRemainPrice)
                .build();
            for (var allProductPaymentFactory : allGamePaymentFactories) {
                if (methods.contains(
                    allProductPaymentFactory.getTargetPaymentMethod())) { // Payment 생성 해야하는 지 여부 확인
                    allProductPaymentFactory.create(allGameProductContext)
                        .ifPresent(payment -> payments.add(payment));
                }
            }
            totalRemainPrice = allGameProductContext.getRemainPrice();
        }

        if (totalRemainPrice != 0) {
            throw new RuntimeException();   //ROLLBACk
        }

        order = orderRepository.findById(order.getId()).get();
        for (var game : games) {
            ordersGamesRepository.save(OrdersGames.builder().game(game).order(order).status(
                OrdersGamesStatus.PENDING).build());
        }
        /*for(var payment : payments) {
            order.getPayments().add(payment);
        }*/

        int pointPrice = 0;  // 포인트 사용량
        int discountPrice = 0;    // 할인된 가격
        int couponPrice = 0;    // 쿠폰 가격
        for (var payment : payments) {
            if (payment.getStatus().equals(PaymentMethod.DISCOUNT)) {
                discountPrice += payment.getPrice();
            } else if (payment.getStatus().equals(PaymentMethod.COUPON)) {
                couponPrice += payment.getPrice();
            } else if (payment.getStatus().equals(PaymentMethod.POINT)) {
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
     * Order 의 상태를 Update 하는 코드 Payment 의 상태를 보고 Order의 상태를 업데이트한다. Payment의 Status가 변경 되었을때 호출 하는
     * 것이 좋다.\ Order 의 모든 Payment의 Status가  Success이면  Order Success 로 변경
     */
    @Transactional
    public OrderDTO updateOrder(Long orderId) throws RuntimeException {
        Order order = orderRepository.findById(orderId).orElseThrow();
        boolean isOrderSuccess = true;
        boolean isOrderCancel = false;
        for (var payment : order.getPayments()) {
            if(payment.getStatus().equals(PaymentStatus.CANCEL)) {
                isOrderCancel = true;
                break;
            } else if(!payment.getStatus().equals(PaymentStatus.SUCCESS)) {
                isOrderSuccess = false;
                break;
            }
        }
        if (isOrderCancel) {
            order.updateStatus(OrderStatus.CANCEL);
            for(var ordersGames : order.getOrdersGames()) {
                ordersGames.updateStatus(OrdersGamesStatus.REFUND);
            }
            for(var payment : order.getPayments()) {
                System.out.println("[주의] payment 환불 구현");
                //TODO: 환불 혹은 취소 만들기 - 전략패턴 사용하기
            }
        }
        if (isOrderSuccess) {
            order.updateStatus(OrderStatus.SUCCESS);
            for(var ordersGames : order.getOrdersGames()) {
                ordersGames.updateStatus(OrdersGamesStatus.PURCHASE);
            }
        }
        return OrderDTO.from(order);
    }
}

