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
import com.teemo.shopping.Order.repository.OrdersPaymentsRepository;
import com.teemo.shopping.Order.repository.PaymentRepository;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.external_api.kakao.KakaopayService;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.dto.GameDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrdersGamesRepository ordersGamesRepository;

    @Autowired
    private OrdersPaymentsRepository ordersPaymentsRepository;
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

    @Autowired
    private KakaopayService kakaopayService;
    // 상속 테이블의 단점 엄청 많아짐

    /**
     * 주문을 생성하는 서비스입니다.
     * 다양한 결제 수단을 사용할 수 있습니다.
     * 할인, 쿠폰, 포인트등을 주문에 적용합니다.
     * 가격의 적용은 (할인, 쿠폰, 포인트, 카카오페이) 순으로 진행됩니다.
     * 모든 결제수단은 다른 결제수단들이 완료되어야 PaymentStatus.SUCCESS 가 됩니다. 안료되지 않은 경우 PaymentStatus.PENDING 입니다.
     * 카카오페이등 외부 API를 사용하여 구현되는 경우
     * 예) 카카오 페이로 3000원 결제가 필요한 경우 지연이 되므로
     *      외부 API 사용으로 지연되므로
     *      Order.status = OrderStatus.PENDING
     *      사용한 모든 Payment.status = OrderStatus.PENDING
     * 이 경우
     * 할인 쿠폰 포인트등 내부 API를 사용하여 구현 되는 경우 바로 처리가 가능하므로
     * 예) 100% 할인으로 남은 결제금액 0원인 경우
     *      Order.status = OrderStatus.SUCCESS
     *      사용한 모든 Payment.status = OrderStatus.SUCCESS
     */
    @Transactional(rollbackOn = RuntimeException.class)
    public Order createOrder(@Valid Account account,        //TODO: DTO 로 변경 예정
        Map<@Valid Game, @Valid Optional<Coupon>> gameCouponMap, PaymentMethod method,
        int point) {
        if (!(method == PaymentMethod.CARD || method == PaymentMethod.KAKAOPAY)) {
            throw new RuntimeException();   // 잘못된 결제 수단
        }

        List<Payment> payments = new ArrayList<>();
        /*List<OrdersGames> ordersGamesList = new ArrayList<>();
        List<OrdersPayments> ordersPaymentsList = new ArrayList<>();*/
        List<CouponPayment.CouponPaymentBuilder> couponPaymentBuilders = new ArrayList<>();
        List<DiscountPayment.DiscountPaymentBuilder> discountPaymentBuilders = new ArrayList<>();
        PointPayment.PointPaymentBuilder pointPaymentBuilder = null;
        int totalPrice = 0;
        int remainTotalPrice = 0;
        for (var gameCouponEntry : gameCouponMap.entrySet()) {
            Game game = gameCouponEntry.getKey();
            Optional<Coupon> couponOptional = gameCouponEntry.getValue();

            totalPrice += game.getPrice();
            int remainPrice = game.getPrice();
            int discountPrice = (int) (remainPrice - Math.round(remainPrice * (1 - game.getDiscount())));

            if (discountPrice != 0) {
                discountPaymentBuilders.add(
                    DiscountPayment.builder().game(game).price(discountPrice).status(PaymentStatus.PENDING));
                remainPrice -= discountPrice;
            }

            if (couponOptional.isPresent()) {
                Coupon coupon = couponOptional.get();
                int couponPrice = -1;
                if (coupon.getMinFulfillPrice() < remainPrice) {
                    throw new RuntimeException();   // 쿠폰 최소 충족 금액 : 충족 안함 다시 해야함
                }
                if (coupon.getMethod() == CouponMethod.PERCENT) {    // improve: 전략패턴
                    couponPrice = (int) (remainPrice * coupon.getAmount());
                    couponPrice = (int) Math.min(coupon.getMaxDiscountPrice(),
                        Math.max(coupon.getMinDiscountPrice(), couponPrice));
                } else if (coupon.getMethod() == CouponMethod.STATIC) {
                    couponPrice = (int) coupon.getAmount();
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
                KakaopayPayment kakaopayPayment = kakaopayPaymentBuilder.price(remainTotalPrice).status(PaymentStatus.PENDING).build();
                kakaopayPaymentRepository.save(kakaopayPayment);
            }
        }
        for (var gameCouponEntry : gameCouponMap.entrySet()) {
            Game game = gameCouponEntry.getKey();
            ordersGamesRepository.save(OrdersGames.builder().game(game).order(order).build());

        }
        for (var payment : payments) {
            ordersPaymentsRepository.save(OrdersPayments.builder().order(order).payment(payment).build());
        }
        return order;
    }

    //TODO: Pending Order 진행 하는 Service 만들어야 함 이서비스는 API 보다는 Redirect 온 후 호출됨


    /**
     *
     * OrderId 진행할 OrderId
     * paymentId 진행된 paymentId
     * @param orderId
     */
    public void updateOrder(Long orderId) {

    }
}

@Data
class GameProduct {
    private GameDTO gameDTO;
    private int remainPrice;
}

interface PaymentProcessor {
    GameProduct process(GameProduct gameProduct);
}

@Component
class DiscountPaymentProcessor implements PaymentProcessor {    //전략 패턴
    @Override
    public GameProduct process(GameProduct gameProduct) {
        int discountPrice = (int) (gameProduct.getRemainPrice() * (1 - gameProduct.getGameDTO().getDiscount()));
        gameProduct.setRemainPrice(gameProduct.getRemainPrice() - discountPrice);
        return gameProduct;
    }
}

// 연산 절차에 대한 고찰
// 가격 책정 정책은 순차적으로 이루
interface PaymentProcessorSequence {
    public void process();
}

