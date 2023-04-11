package com.teemo.shopping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsCoupons;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsCouponsRepository;
import com.teemo.shopping.account.repository.AccountsOwnGamesRepository;
import com.teemo.shopping.account.service.AccountAuthenticationService;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.repository.CouponRepository;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveRequest;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.OrdersGamesRepository;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.OrderService;
import com.teemo.shopping.util.ConverterToMultiValueMap;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest(classes = Main.class)
public class OrderTest {
    // Todo: Kakao Pay API Test 작성
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AccountAuthenticationService accountAuthenticationService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private AccountsOwnGamesRepository accountsOwnGamesRepository;
    @Autowired
    private AccountsCouponsRepository accountsCouponsRepository;
    @Autowired
    private OrdersGamesRepository ordersGamesRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @PersistenceContext
    private EntityManager em;
    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @Test
    void test() {
        var kakao = KakaopayAPIApproveRequest.builder()
            .tid("123")
            .cid("213")
            .partnerOrderId("12451")
            .pgToken("1d21j0")
            .partnerUserId("123User")
            .totalAmount(10000)
            .build();
        var c = ConverterToMultiValueMap.convertToFormData(kakao);
    }


    void CouponPaymentService() {

    }
    @Test
    void order() {
        Session session = em.unwrap(Session.class);
        Account account = Account.builder()
            .username("username")
            .password("password")
            .point(50000)
            .build();
        account = accountRepository.save(account);

        List<Game> games = new ArrayList<>();

        Game game1 = Game.builder()
            .name("테트리스")
            .description("슬라브 게임")
            .price(10000)
            .discountPercent(100)
            .build();
        Game game2 = Game.builder()
            .name("마인크래프트")
            .description("Lets Dig Up")
            .price(30000)
            .discountPercent(100)
            .build();
        Game game3 = Game.builder()
            .name("스타크래프트")
            .description("Use Map")
            .price(7000)
            .discountPercent(100)
            .build();

        game1 = gameRepository.save(game1);
        game2 = gameRepository.save(game2);
        game3 = gameRepository.save(game3);

        Coupon coupon = Coupon
            .builder()
            .minFulfillPrice(5000)
            .name("5000 WON")
            .description("1년 기념 5000원 세일")
            .method(CouponMethod.STATIC)
            .amount(5000)
            .build();
        couponRepository.save(coupon);

        HashMap<Long, Optional<Long>> gameCouponIdMap = new HashMap<>();
        gameCouponIdMap.put(game1.getId(), Optional.of(coupon.getId()));
        gameCouponIdMap.put(game2.getId(), Optional.empty());
        gameCouponIdMap.put(game3.getId(), Optional.empty());

        AccountsCoupons accountsCoupons = AccountsCoupons
            .builder()
            .account(account)
            .coupon(coupon)
            .amount(1)
            .build();
        accountsCoupons = accountsCouponsRepository.save(accountsCoupons);

        var orderId = orderService.createOrder(account.getId(), 5000, List.of(
            PaymentMethod.COUPON,
            PaymentMethod.POINT,
            PaymentMethod.KAKAOPAY,
            PaymentMethod.DISCOUNT,
            PaymentMethod.POINT
        ), gameCouponIdMap);

        var accountsHasGames = accountsOwnGamesRepository.findAll();
        account= accountRepository.findById(account.getId()).get();
        var order = orderRepository.findById(orderId).get();
        var payments = paymentRepository.findAllByOrder(order);
        var ordersGames = ordersGamesRepository.findAllByOrder(order);

        assertEquals(game1, accountsOwnGamesRepository.findByAccountAndGame(account, game1).get()
            .getGame());
        assertEquals(game2, accountsOwnGamesRepository.findByAccountAndGame(account, game2).get()
            .getGame());
        assertEquals(game3, accountsOwnGamesRepository.findByAccountAndGame(account, game3).get()
            .getGame());

        orderService.refundOrder(order.getId());
        accountsCoupons = accountsCouponsRepository.findByAccountAndCoupon(account, coupon).get();
        order = orderRepository.findById(order.getId()).get();
        payments = paymentRepository.findAllByOrder(order);
        ordersGames = ordersGamesRepository.findAllByOrder(order);
    }

}
