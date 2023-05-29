package com.teemo.shopping.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.teemo.shopping.Main;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.service.AccountAuthenticationService;
import com.teemo.shopping.account.service.AccountService;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.dto.AddCouponRequest;
import com.teemo.shopping.coupon.service.CouponService;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.game.service.GameService;
import com.teemo.shopping.payment.domain.CouponPayment;
import com.teemo.shopping.payment.domain.enums.PaymentStates;
import com.teemo.shopping.order.service.OrderCreateService.OrderOption;
import com.teemo.shopping.order.service.OrderCreateService.PreparedData;
import com.teemo.shopping.order.service.OrderService;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.payment.service.CouponPaymentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = Main.class)
public class OrderTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountAuthenticationService accountAuthenticationService;
    @Autowired
    private GameService gameService;
    @Autowired
    private CouponPaymentService couponPaymentService;
    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void login() {

    }

    @AfterEach
    public void logout() {

    }

    @Test
    @Transactional
    void createCouponPayment() throws Exception {
        OrderOption orderOption = OrderOption.of(
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
        Long accountId = accountAuthenticationService.register("teemo", "teemo");
        Long gameId = gameService.add(GameDTO.builder()
            .price(5000)
            .name("마인크래프트")
            .description("땅 파는 게임")
            .build());
        Long couponId = couponService.add(AddCouponRequest
            .builder()
            .amount(3000d)
            .minFulfillPrice(3000)
            .name("마인크래프트 할인 쿠폰")
            .canApplyToAll(true)
            .method(CouponMethod.STATIC)
            .build());

        Account account = entityManager.find(Account.class, accountId);
        Game game = entityManager.find(Game.class, gameId);
        Coupon coupon = entityManager.find(Coupon.class, couponId);
        PreparedData preparedData = PreparedData.of(
            account,
            null,
            Map.of(game, Optional.of(coupon)),
            0
        );
        OrderCreateContext orderCreateContext = OrderCreateContext.builder()
            .orderOption(orderOption)
            .preparedData(preparedData)
            .amount(game.getPrice())
            .game(Optional.of(game))
            .build();
        CouponPayment couponPayment = (CouponPayment)couponPaymentService.create(orderCreateContext);

        assertEquals(couponPayment.getAccount(), account);
        assertEquals(couponPayment.getCoupon(), coupon);
        assertEquals(couponPayment.getAmount(), coupon.getAmount().intValue());
        assertEquals(couponPayment.getGame(), game);
        assertEquals(couponPayment.getState(), PaymentStates.PENDING);
    }
    @Test
    @Transactional
    void payCouponPayment() throws Exception {
        OrderOption orderOption = OrderOption.of(
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
        Long accountId = accountAuthenticationService.register("teemo2", "teemo2");
        Long gameId = gameService.add(GameDTO.builder()
            .price(5000)
            .name("마인크래프트")
            .description("땅 파는 게임")
            .build());
        Long couponId = couponService.add(AddCouponRequest
            .builder()
            .amount(3000d)
            .minFulfillPrice(3000)
            .name("마인크래프트 할인 쿠폰")
            .canApplyToAll(true)
            .method(CouponMethod.STATIC)
            .build());

        Account account = entityManager.find(Account.class, accountId);
        Game game = entityManager.find(Game.class, gameId);
        Coupon coupon = entityManager.find(Coupon.class, couponId);
        PreparedData preparedData = PreparedData.of(
            account,
            null,
            Map.of(game, Optional.of(coupon)),
            0
        );
        OrderCreateContext orderCreateContext = OrderCreateContext.builder()
            .orderOption(orderOption)
            .preparedData(preparedData)
            .amount(game.getPrice())
            .game(Optional.of(game))
            .build();
        CouponPayment couponPayment = (CouponPayment)couponPaymentService.create(orderCreateContext);
        entityManager.persist(couponPayment);
        couponPaymentService.pay(couponPayment.getId());
        couponPayment = entityManager.find(CouponPayment.class, couponPayment.getId());


    }

}
