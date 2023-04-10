package com.teemo.shopping.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.teemo.shopping.Main;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.repository.OrderRepository;
import com.teemo.shopping.Order.repository.OrdersGamesRepository;
import com.teemo.shopping.Order.repository.PaymentRepository;
import com.teemo.shopping.Order.service.OrderService;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsCoupons;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsCouponsRepository;
import com.teemo.shopping.account.repository.AccountsGamesRepository;
import com.teemo.shopping.account.service.AccountService;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.repository.CouponRepository;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveRequest;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import com.teemo.shopping.util.ConverterToMultiValueMap;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.PersistenceUtil;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Persistent;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(classes = Main.class)
public class OrderTest {
    // Todo: Kakao Pay API Test 작성
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private AccountsGamesRepository accountsGamesRepository;
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
            .discountPercent(50)
            .build();
        Game game2 = Game.builder()
            .name("마인크래프트")
            .description("Lets Dig Up")
            .price(30000)
            .discountPercent(20)
            .build();
        Game game3 = Game.builder()
            .name("스타크래프트")
            .description("Use Map")
            .price(7000)
            .discountPercent(30)
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

        HashMap<Game, Optional<Coupon>> gameCouponMap = new HashMap<>();
        gameCouponMap.put(game1, Optional.of(coupon));
        gameCouponMap.put(game2, Optional.empty());
        gameCouponMap.put(game3, Optional.empty());

        AccountsCoupons accountsCoupons = AccountsCoupons
            .builder()
            .account(account)
            .coupon(coupon)
            .amount(1)
            .build();
        accountsCoupons = accountsCouponsRepository.save(accountsCoupons);

        var orderId = orderService.createOrder(account, gameCouponMap, List.of(
            PaymentMethod.COUPON,
            PaymentMethod.POINT,
            PaymentMethod.KAKAOPAY,
            PaymentMethod.DISCOUNT,
            PaymentMethod.POINT
        ), 5000);

        var accountsGames = accountsGamesRepository.findAll();
        account= accountRepository.findById(account.getId()).get();
        var order = orderRepository.findById(orderId).get();
        var payments = paymentRepository.findAllByOrder(order);
        var ordersGames = ordersGamesRepository.findAllByOrder(order);

        assertEquals(game1, accountsGamesRepository.findByAccountAndGame(account, game1).get()
            .getGame());
        assertEquals(game2, accountsGamesRepository.findByAccountAndGame(account, game2).get()
            .getGame());
        assertEquals(game3, accountsGamesRepository.findByAccountAndGame(account, game3).get()
            .getGame());

        orderService.refundOrder(order.getId());
        accountsCoupons = accountsCouponsRepository.findByAccountAndCoupon(account, coupon).get();
        order = orderRepository.findById(order.getId()).get();
        payments = paymentRepository.findAllByOrder(order);
        ordersGames = ordersGamesRepository.findAllByOrder(order);
    }

}
