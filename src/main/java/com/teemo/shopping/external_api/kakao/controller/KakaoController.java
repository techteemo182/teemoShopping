package com.teemo.shopping.external_api.kakao.controller;

import com.teemo.shopping.order.domain.KakaopayPayment;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.dto.KakaopayRedirectParameter;
import com.teemo.shopping.order.dto.KakaopayRedirectParameter.KakaopayRedirectType;
import com.teemo.shopping.order.repository.KakaopayPaymentRepository;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.service.KakaopayPaymentService;
import com.teemo.shopping.order.service.OrderService;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsCoupons;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsCouponsRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.repository.CouponRepository;
import com.teemo.shopping.external_api.kakao.dto.KakaoRedirectParameter;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveResponse;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Persistent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/kakao")
public class KakaoController {
    @Autowired
    private KakaopayPaymentService kakaopayPaymentService;

    @GetMapping("/success")
    public Optional<KakaopayAPIApproveResponse> success(KakaoRedirectParameter kakaoRedirectParameter) {

        Optional<KakaopayAPIApproveResponse> response = Optional.empty();
        try {
            response = kakaopayPaymentService.onKakaopayRedirectResponse(
                KakaopayRedirectParameter.builder().pgToken(kakaoRedirectParameter.getPgToken())
                    .type(KakaopayRedirectType.SUCCESS)
                    .partnerOrderId(kakaoRedirectParameter.getPartnerOrderId())
                    .partnerUserId(kakaoRedirectParameter.getPartnerUserId()).build());
        } catch (Exception e) {
            System.out.println("[Wrong]");
        }
        return response;
    }

    @GetMapping("/cancel")
    public void cancel(KakaoRedirectParameter kakaoRedirectReqeust) {
        try {
            kakaopayPaymentService.onKakaopayRedirectResponse(
                KakaopayRedirectParameter.builder().type(KakaopayRedirectType.CANCEL)
                    .partnerOrderId(kakaoRedirectReqeust.getPartnerOrderId())
                    .partnerUserId(kakaoRedirectReqeust.getPartnerUserId()).build());
        } catch (Exception e) {
            System.out.println("[Wrong]");
        }
    }

    @GetMapping("/fail")
    public void fail(KakaoRedirectParameter kakaoRedirectReqeust) {
        try {
            kakaopayPaymentService.onKakaopayRedirectResponse(
                KakaopayRedirectParameter.builder().type(KakaopayRedirectType.FAIL)
                    .partnerOrderId(kakaoRedirectReqeust.getPartnerOrderId())
                    .partnerUserId(kakaoRedirectReqeust.getPartnerUserId()).build());
        } catch (Exception e) {
            System.out.println("[Wrong]");
        }
    }

    @Persistent
    private EntityManager em;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private AccountsCouponsRepository accountsCouponsRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private KakaopayPaymentRepository kakaopayPaymentRepository;

    @GetMapping("/test")
    public RedirectView test() {
        Account account = Account
            .builder()
            .username("test")
            .password("test")
            .point(1000)
            .build();
        accountRepository.save(account);

        Map<Long, Long> gameCouponIdMap = new HashMap<>();
        List<Long> gameIds = new ArrayList<>();
        Game game1 = Game.builder()
            .price(3000)
            .name("ZAO")
            .ratingCount(12)
            .ratingAvg(3)
            .description("재미있는 게임")
            .build();
        Game game2 = Game.builder()
            .price(6000)
            .name("ZAO2")
            .ratingCount(12)
            .ratingAvg(3)
            .description("재미있는 게임")
            .build();
        gameRepository.save(game1);
        gameRepository.save(game2);

        Coupon coupon = Coupon.builder()
            .amount(3000)
            .method(CouponMethod.STATIC)
            .minFulfillPrice(3000)
            .description("3000원 쿠폰!")
            .build();
        couponRepository.save(coupon);
        accountsCouponsRepository.save(AccountsCoupons.builder()
            .account(account)
            .coupon(coupon)
            .amount(1)
            .build());
        gameIds.add(game1.getId());
        gameIds.add(game2.getId());
        gameCouponIdMap.put(game2.getId(), coupon.getId());

        List<PaymentMethod> methods = new ArrayList<>();
        methods.add(PaymentMethod.COUPON);
        methods.add(PaymentMethod.POINT);
        methods.add(PaymentMethod.KAKAOPAY);

        int point = 500;
        var result = orderService.createOrder(account.getId(),
            point,
            methods,
            gameIds,
            gameCouponIdMap);
        Order order = orderRepository.findById(result).get();
        KakaopayPayment kakaopayPayment = kakaopayPaymentRepository.findByOrder(order).get();
        return new RedirectView(kakaopayPayment.getNextRedirectPcUrl());
    }
}

