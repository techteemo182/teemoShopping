package com.teemo.shopping.external_api.kakao.controller;

import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.dto.KakaopayRedirectParameter;
import com.teemo.shopping.Order.dto.KakaopayRedirectParameter.KakaopayRedirectType;
import com.teemo.shopping.Order.service.OrderService;
import com.teemo.shopping.Order.service.PaymentService;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsCoupons;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsCouponsRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.repository.CouponRepository;
import com.teemo.shopping.external_api.kakao.KakaopayService;
import com.teemo.shopping.external_api.kakao.dto.KakaoRedirectRequest;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Persistent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/kakao")
public class KakaoController {
    @Autowired
    private PaymentService paymentService;

    // Todo: 리다이렉트가 프론트에서도 되는데 해결방법 찾기
    @GetMapping("/success")
    public void success(KakaoRedirectRequest kakaoRedirectRequest) {
        try {
            paymentService.onKakaopayRedirectResponse(
                KakaopayRedirectParameter.builder().pgToken(kakaoRedirectRequest.getPgToken())
                    .type(KakaopayRedirectType.SUCCESS)
                    .partnerOrderId(kakaoRedirectRequest.getPartnerOrderId())
                    .partnerUserId(kakaoRedirectRequest.getPartnerUserId()).build());
        } catch (Exception e) {
            System.out.println("[Wrong]");
        }
    }

    @GetMapping("/cancel")
    public void cancel(KakaoRedirectRequest kakaoRedirectReqeust) {
        try {
            paymentService.onKakaopayRedirectResponse(
                KakaopayRedirectParameter.builder().type(KakaopayRedirectType.CANCEL)
                    .partnerOrderId(kakaoRedirectReqeust.getPartnerOrderId())
                    .partnerUserId(kakaoRedirectReqeust.getPartnerUserId()).build());
        } catch (Exception e) {
            System.out.println("[Wrong]");
        }
    }

    @GetMapping("/fail")
    public void fail(KakaoRedirectRequest kakaoRedirectReqeust) {
        try {
            paymentService.onKakaopayRedirectResponse(
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

    @GetMapping("/test")
    public RedirectView test() {
        Account account = Account
            .builder()
            .username("test")
            .password("test")
            .point(1000)
            .build();
        accountRepository.save(account);

        Map<@Valid Game, @Valid Optional<Coupon>> gameCouponMap = new HashMap<>();
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
        gameCouponMap.put(game1, Optional.empty());
        gameCouponMap.put(game2, Optional.of(coupon));

        SortedSet<PaymentMethod> methods = new TreeSet<>();
        methods.add(PaymentMethod.COUPON);
        methods.add(PaymentMethod.POINT);
        methods.add(PaymentMethod.KAKAOPAY);

        int point = 500;
        var result = orderService.createOrder(account, gameCouponMap, methods, point);
        return new RedirectView(result.getNextRedirectPcUrl());
    }
}

