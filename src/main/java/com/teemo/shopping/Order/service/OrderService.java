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
import com.teemo.shopping.Order.repository.OrdersGamesRepository;
import com.teemo.shopping.Order.repository.OrdersPaymentsRepository;
import com.teemo.shopping.Order.repository.PaymentRepository;
import com.teemo.shopping.Order.service.payment_factory.OrderContext;
import com.teemo.shopping.Order.service.payment_factory.OrderedSequencePaymentGenerator;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.external_api.kakao.KakaopayService;
import com.teemo.shopping.game.domain.Game;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private OrderedSequencePaymentGenerator orderedSequencePaymentGenerator;

    /**
     * 주문을 생성하는 서비스입니다. 다양한 결제 수단을 사용할 수 있습니다. 할인, 쿠폰, 포인트등을 주문에 적용합니다. 가격의 적용은 (할인, 쿠폰, 포인트, 카카오페이)
     * 순으로 진행됩니다. 모든 결제수단은 다른 결제수단들이 완료되어야 PaymentStatus.SUCCESS 가 됩니다. 안료되지 않은 경우
     * PaymentStatus.PENDING 입니다. 카카오페이등 외부 API를 사용하여 구현되는 경우 예) 카카오 페이로 3000원 결제가 필요한 경우 지연이 되므로 외부
     * API 사용으로 지연되므로 Order.status = OrderStatus.PENDING 사용한 모든 Payment.status = OrderStatus.PENDING
     * 이 경우 할인 쿠폰 포인트등 내부 API를 사용하여 구현 되는 경우 바로 처리가 가능하므로 예) 100% 할인으로 남은 결제금액 0원인 경우 Order.status =
     * OrderStatus.SUCCESS 사용한 모든 Payment.status = OrderStatus.SUCCESS
     */
    @Transactional(rollbackOn = RuntimeException.class)
    public Order createOrder(@Valid Account account,    //TODO: DTO 로 변경 예정
        Map<@Valid Game, @Valid Optional<Coupon>> gameCouponMap, List<PaymentMethod> methods,
        int point) {
        int totalPrice = gameCouponMap.entrySet().stream()
            .mapToInt(entry -> entry.getKey().getPrice()).reduce(0, (ingPrice, v) -> ingPrice + v);
        List<Game> games = gameCouponMap.entrySet().stream().map(entry -> entry.getKey()).toList();
        Order order = Order.builder()
            .account(account)
            .totalPrice(totalPrice)
            .status(OrderStatus.PENDING)
            .build();
        OrderContext context = OrderContext.builder()
            .gameCouponMap(gameCouponMap)
            .account(account)
            .order(order)
            .paymentMethods(methods)
            .games(games)
            .point(point)
            .build();
        List<Payment> payments = orderedSequencePaymentGenerator.process(context);
        for (var payment : payments) {
            ordersPaymentsRepository.save(OrdersPayments.builder().payment(payment).order(order).build());
        }
        for (var game : games) {
            ordersGamesRepository.save(OrdersGames.builder().game(game).order(order).build());
        }
        return order;
    }

    //TODO: Pending Order 진행 하는 Service 만들어야 함 이서비스는 API 보다는 Redirect 온 후 호출됨


    /**
     * OrderId 진행할 OrderId paymentId 진행된 paymentId
     *
     * @param orderId
     */
    public void updateOrder(Long orderId) {

    }
}

// 한계점 Context가 이 createOrder가 의도하는 input에 의존성이 있음
// 그러나 동적인 변수를 추가한다 하더라도 높은 pro 를 기대 할 수 없음


