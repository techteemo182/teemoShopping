package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.CardPayment;
import com.teemo.shopping.Order.domain.CouponPayment;
import com.teemo.shopping.Order.domain.DiscountPayment;
import com.teemo.shopping.Order.domain.KakaopayPayment;
import com.teemo.shopping.Order.domain.Order;
import com.teemo.shopping.Order.domain.OrdersGames;
import com.teemo.shopping.Order.domain.OrdersPayments;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.PointPayment;
import com.teemo.shopping.Order.domain.enums.OrderStatus;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.repository.OrdersGamesRepository;
import com.teemo.shopping.Order.repository.PaymentRepository;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.game.domain.Game;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OrderService {

    @Autowired
    private OrdersGamesRepository ordersGamesRepository;
    @Autowired
    private PaymentRepository<CouponPayment> couponPaymentRepository;
    @Autowired
    private PaymentRepository<DiscountPayment> discountPaymentRepository;
    @Autowired
    private PaymentRepository<CardPayment> cardPaymentRepository;
    @Autowired
    private PaymentRepository<KakaopayPayment> kakaopayPaymentRepository;
    @Autowired
    private PaymentRepository<PointPayment> pointPaymentRepository;
    @Autowired
    private PaymentRepository<Payment> paymentRepository;
    // 상속 테이블의 단점 엄청 많아짐

    @Transactional(rollbackOn = RuntimeException.class)
    public Order createOrder(@Valid Account account,
        Map<@Valid Game, @Valid Optional<Coupon>> gameCouponMap, PaymentMethod method,
        double point) {
        // 간단한 알고리즘
        // 게임을 구매하려면 구입 방법등이 있어야한다.
        // 게임을 고른다 결제 수단을 선택한다
        // 결제수단에는 카드, 카카오, 포인트 등 여러가지 방법등이 있고
        // 포인트, 쿠폰, 할인의 경우 카드, 카카오와 병행하여 사용가능하다.
        // 카드와 카카오는 같이 사용할 수 없다.
        // 포인트, 쿠폰, 할인은 중복하여 사용할 수 있다.
        // 카드와 카카오의 경우 바로 결제가 되지 아니하고 Third party API 사용가능
        // 결제 순서
        // 1. 할인율 적용
        // 2. 쿠폰 할인 적용
        // 3. 포인트 적용
        // 3. 카카오페이나 카드를 사용하여 전액 결제

        if (!(method == PaymentMethod.CARD || method == PaymentMethod.KAKAOPAY)) {
            throw new RuntimeException();   // 잘못된 결제 수단
        }

        List<Payment> payments = new ArrayList<>();
        List<OrdersGames> ordersGamesList = new ArrayList<>();
        List<OrdersPayments> ordersPaymentsList = new ArrayList<>();
        List<CouponPayment.CouponPaymentBuilder> couponPaymentBuilders = new ArrayList<>();
        List<DiscountPayment.DiscountPaymentBuilder> discountPaymentBuilders = new ArrayList<>();
        PointPayment.PointPaymentBuilder pointPaymentBuilder = null;
        double totalPrice = 0;
        double remainTotalPrice = 0;
        for (var gameCouponEntry : gameCouponMap.entrySet()) {
            Game game = gameCouponEntry.getKey();
            Optional<Coupon> couponOptional = gameCouponEntry.getValue();

            totalPrice += game.getPrice();
            double remainPrice = game.getPrice();
            double discountPrice = remainPrice - Math.round(remainPrice * (1 - game.getDiscount()));

            if (discountPrice != 0) {
                discountPaymentBuilders.add(
                    DiscountPayment.builder().game(game).price(discountPrice).status(PaymentStatus.PENDING));
                remainPrice -= discountPrice;
            }

            if (couponOptional.isPresent()) {
                Coupon coupon = couponOptional.get();
                double couponPrice = -1;
                if (coupon.getMinFulfillPrice() < remainPrice) {
                    throw new RuntimeException();   // 쿠폰 최소 충족 금액 : 충족 안함 다시 해야함
                }
                if (coupon.getMethod() == CouponMethod.PERCENT) {    // improve: 전략패턴
                    couponPrice = remainPrice * coupon.getAmount();
                    couponPrice = Math.min(coupon.getMaxDiscountPrice(),
                        Math.max(coupon.getMinDiscountPrice(), couponPrice));
                } else if (coupon.getMethod() == CouponMethod.STATIC) {
                    couponPrice = coupon.getAmount();
                }
                couponPaymentBuilders.add(
                    CouponPayment.builder().game(game).coupon(coupon).price(couponPrice).status(PaymentStatus.PENDING));
            }
            remainTotalPrice += remainPrice;
        }
        if (remainTotalPrice != 0) {
            point = Math.min(point, remainTotalPrice);
            if (account.getPoint() < point) {
                throw new RuntimeException(); // Wrong Point 포인트 부족
            }
            remainTotalPrice -= point;
            pointPaymentBuilder = PointPayment.builder().price(point).status(PaymentStatus.PENDING);
        }

        Order order;
        PaymentStatus paymentStatus = PaymentStatus.PENDING;
        OrderStatus orderStatus = OrderStatus.PENDING;
        if (remainTotalPrice == 0) { // 무료 빠른 주문 완성
            paymentStatus = PaymentStatus.SUCCESS;
            orderStatus = OrderStatus.SUCCESS;
        }

        for (var couponPaymentBuilder : couponPaymentBuilders) {
            CouponPayment couponPayment = couponPaymentBuilder.status(paymentStatus).build();
            couponPaymentRepository.save(couponPayment);
            payments.add(couponPayment);
        }
        for (var discountPaymentBuilder : discountPaymentBuilders) {
            DiscountPayment discountPayment = discountPaymentBuilder.status(paymentStatus).build();
            discountPaymentRepository.save(discountPayment);
            payments.add(discountPayment);
        }
        if (pointPaymentBuilder != null) {
            PointPayment pointPayment = pointPaymentBuilder.status(paymentStatus).build();
            pointPaymentRepository.save(pointPayment);
            payments.add(pointPayment);
        }

        order = Order.builder().account(account).totalPrice(totalPrice).status(orderStatus).build();

        if(remainTotalPrice != 0) {
            if(method == PaymentMethod.CARD) {

            }
            else if(method == PaymentMethod.KAKAOPAY) {
                KakaopayPayment.KakaopayPaymentBuilder kakaopayPaymentBuilder = KakaopayPayment.builder();
                kakaopayPaymentBuilder.price(remainTotalPrice).status(PaymentStatus.PENDING);

                //결제 준비 여기서 하고
                //결제 승인 은 다음
                /**
                 *  진행 순서 서버에서 Request 를  Response 받아서 Redirect 하라고 지시
                 */

            }
        }
        for (var gameCouponEntry : gameCouponMap.entrySet()) {
            Game game = gameCouponEntry.getKey();
            ordersGamesList.add(OrdersGames.builder().game(game).order(order).build());
        }
        for (var payment : payments) {
            ordersPaymentsList.add(OrdersPayments.builder().order(order).payment(payment).build());
        }
        return order;
    }
}
