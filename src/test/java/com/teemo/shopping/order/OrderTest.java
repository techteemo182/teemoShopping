package com.teemo.shopping.order;

import com.teemo.shopping.Main;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.dto.CreateOrderReturn;
import com.teemo.shopping.Order.service.OrderService;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIReadyRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIReadyResponse;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.util.ConverterToMultiValueMap;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Persistent;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(classes = Main.class)
public class OrderTest {
    // Todo: Kakao Pay API Test 작성
    @Autowired
    private OrderService orderService;
    @Persistent
    private EntityManager em;

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

}
