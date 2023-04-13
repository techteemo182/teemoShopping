package com.teemo.shopping.order;

import com.teemo.shopping.Main;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.service.AccountAuthenticationService;
import com.teemo.shopping.account.service.AccountService;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.coupon.service.CouponService;
import com.teemo.shopping.game.domain.Game;
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
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        List<Game> games = new ArrayList<>();

        GameDTO game1 = GameDTO.builder().name("테트리스").description("슬라브 게임").price(10000)
            .discountPercent(100d).build();
        GameDTO game2 = GameDTO.builder().name("마인크래프트").description("Lets Dig Up").price(30000)
            .discountPercent(100d).build();
        GameDTO game3 = GameDTO.builder().name("스타크래프트").description("Use Map").price(7000)
            .discountPercent(100d).build();
        Long gameId1 = gameService.add(game1);
        Long gameId2 = gameService.add(game2);
        Long gameId3 = gameService.add(game3);

        CouponDTO coupon = CouponDTO.builder().minFulfillPrice(5000).name("5000 WON")
            .description("1년 기념 5000원 세일").method(CouponMethod.STATIC).amount(5000)
            .expiredAt(LocalDateTime.now().plusDays(10)).build();
        Long couponId = couponService.add(coupon);
        couponService.addGame(couponId, gameId1);
        couponService.addGame(couponId, gameId2);
        couponService.addGame(couponId, gameId3);
        HashMap<Long, Optional<Long>> gameCouponIdMap = new HashMap<>();
        gameCouponIdMap.put(gameId1, Optional.of(couponId));
        gameCouponIdMap.put(gameId2, Optional.empty());
        gameCouponIdMap.put(gameId3, Optional.empty());

        accountService.addCoupon(accountId, couponId, 1);

        var orderId = orderService.createOrder(accountId, 50000,
            List.of(PaymentMethod.COUPON, PaymentMethod.POINT, PaymentMethod.KAKAOPAY,
                PaymentMethod.DISCOUNT), gameCouponIdMap);
        var orderDTO = orderService.get(orderId);
    }

}
