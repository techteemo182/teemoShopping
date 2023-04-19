package com.teemo.shopping.order;

import com.teemo.shopping.Main;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.service.AccountAuthenticationService;
import com.teemo.shopping.account.service.AccountService;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.dto.AddCouponRequest;
import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.coupon.service.CouponService;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.game.service.GameService;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Main.class)
public class OrderTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AccountAuthenticationService accountAuthenticationService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private CouponService couponService;

    @Autowired
    private AccountService accountService;
    @Autowired
    private PaymentRepository paymentRepository;
    @PersistenceContext
    private EntityManager em;

    @Test
    void order() throws Exception {
        Long accountId = accountAuthenticationService.register("teemo341", "teemo341");
        accountService.addPoint(accountId, 50000);
        List<Long> gameIds = new ArrayList<>();

        GameDTO game1 = GameDTO.builder().name("테트리스").description("슬라브 게임").price(10000)
            .discountPercent(100d).build();
        GameDTO game2 = GameDTO.builder().name("마인크래프트").description("Lets Dig Up").price(30000)
            .discountPercent(100d).build();
        GameDTO game3 = GameDTO.builder().name("스타크래프트").description("Use Map").price(7000)
            .discountPercent(100d).build();
        Long gameId1 = gameService.add(game1);
        Long gameId2 = gameService.add(game2);
        Long gameId3 = gameService.add(game3);
        gameIds.add(gameId1);
        gameIds.add(gameId2);
        gameIds.add(gameId3);
        AddCouponRequest couponRequest = AddCouponRequest.builder().minFulfillPrice(5000).name("5000 WON")
            .description("1년 기념 5000원 세일").method(CouponMethod.STATIC).amount(5000d)
            .expiredAt(LocalDateTime.now().plusDays(10)).canApplyToAll(false).build();

        Long couponId = couponService.add(couponRequest);
        couponService.addGame(couponId, gameId1);
        couponService.addGame(couponId, gameId2);
        couponService.addGame(couponId, gameId3);

        HashMap<Long, Long> gameCouponIdMap = new HashMap<>();
        gameCouponIdMap.put(gameId1, couponId);

        accountService.addCoupon(accountId, couponId, 1);

        var orderId = orderService.addOrder(accountId, 50000,
            List.of(PaymentMethod.COUPON, PaymentMethod.POINT, PaymentMethod.KAKAOPAY,
                PaymentMethod.DISCOUNT), gameIds, gameCouponIdMap, "naver.com");
        var orderDTO = orderService.get(orderId);
    }

}
