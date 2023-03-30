package com.teemo.shopping.Order;

import com.teemo.shopping.account.Account;
import com.teemo.shopping.game.Game;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Transactional
    public void purchaseGame(Account account, Game game, List<Payment> payments) {
        // 간단한 알고리즘
        // 게임을 구매하려면 구입 방법등이 있어야한다.
        // 게임을 고른다 결제 수단을 선택한다
        // 결제수단에는 카드, 카카오, 포인트 등 여러가지 방법등이 있고
        // 포인트, 쿠폰, 할인의 경우 카드, 카카오와 병행하여 사용가능하다.
        // 카드와 카카오는 같이 사용할 수 없다.
        // 포인트, 쿠폰, 할인은 중복하여 사용할 수 있다.
        // 카드와 카카오의 경우 바로 결제가 되지 아니하고 Third party 프로그램 사용가능
       /* Order order = Order
            .builder()
            .account(account)
            .totalPrice(game)
            .build();*/
    }
}
