package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.Order;
import com.teemo.shopping.Order.domain.OrdersGames;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.PaymentMethod;
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
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private OrdersGamesRepository ordersGamesRepository;
    @Transactional
    public void purchaseGame(@Valid Account account,
        Map<@Valid Game, @Valid Optional<Coupon>> gameCouponMap, @Valid PaymentMethod method) {
        // 간단한 알고리즘
        // 게임을 구매하려면 구입 방법등이 있어야한다.
        // 게임을 고른다 결제 수단을 선택한다
        // 결제수단에는 카드, 카카오, 포인트 등 여러가지 방법등이 있고
        // 포인트, 쿠폰, 할인의 경우 카드, 카카오와 병행하여 사용가능하다.
        // 카드와 카카오는 같이 사용할 수 없다.
        // 포인트, 쿠폰, 할인은 중복하여 사용할 수 있다.
        // 카드와 카카오의 경우 바로 결제가 되지 아니하고 Third party 프로그램 사용가능

        double totalPrice = 0;
        List<Payment> payments = new ArrayList<>();
        for (var gameCouponEntry : gameCouponMap.entrySet()) {
            Game game = gameCouponEntry.getKey();
            double gameActualPrice = Math.round(
                game.getPrice() * (1 - game.getDiscount())); // 반올림해서 금액 정액적으로 변경
            totalPrice += gameActualPrice;
        }

        Order order = Order.builder().account(account).totalPrice(totalPrice).build();

        List<OrdersGames> orderHasGamesList = new ArrayList<>();
        for (var gameCouponEntry : gameCouponMap.entrySet()) {
            Game game = gameCouponEntry.getKey();
            OrdersGames.builder().order(order).game(game).build();


        }


    }
}