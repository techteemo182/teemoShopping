package com.teemo.shopping.external_api.kakao.controller;

import com.teemo.shopping.order.domain.KakaopayPayment;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.parameter.KakaopayRedirectParameter;
import com.teemo.shopping.order.service.parameter.KakaopayRedirectParameter.KakaopayRedirectType;
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
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityManager;
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
    @Operation(operationId = "카카오페이 리다이렉트 성공", summary = "카카오페이 리다이렉트 성공", tags = {"카카오 리다이렉트"})
    @GetMapping("/success")
    public RedirectView success(KakaoRedirectParameter kakaoRedirectParameter) {

        Optional<KakaopayAPIApproveResponse> response = Optional.empty();
        try {
            response = kakaopayPaymentService.onKakaopayRedirectResponse(
                KakaopayRedirectParameter.builder().pgToken(kakaoRedirectParameter.getPgToken())
                    .type(KakaopayRedirectType.SUCCESS)
                    .partnerOrderId(kakaoRedirectParameter.getPartnerOrderId())
                    .partnerUserId(kakaoRedirectParameter.getPartnerUserId()).build());
        } catch (Exception e) {

        }
        return new RedirectView(kakaoRedirectParameter.getRedirect());
    }

    @Operation(operationId = "카카오페이 리다이렉트 취소", summary = "카카오페이 리다이렉트 취소", tags = {"카카오 리다이렉트"})
    @GetMapping("/cancel")
    public RedirectView cancel(KakaoRedirectParameter kakaoRedirectParameter) {
        try {
            kakaopayPaymentService.onKakaopayRedirectResponse(
                KakaopayRedirectParameter.builder().type(KakaopayRedirectType.CANCEL)
                    .partnerOrderId(kakaoRedirectParameter.getPartnerOrderId())
                    .partnerUserId(kakaoRedirectParameter.getPartnerUserId()).build());
        } catch (Exception e) {

        }
        return new RedirectView(kakaoRedirectParameter.getRedirect());
    }
    @Operation(operationId = "카카오페이 리다이렉트 실패", summary = "카카오페이 리다이렉트 실패", tags = {"카카오 리다이렉트"})
    @GetMapping("/fail")
    public RedirectView fail(KakaoRedirectParameter kakaoRedirectParameter) {
        try {
            kakaopayPaymentService.onKakaopayRedirectResponse(
                KakaopayRedirectParameter.builder().type(KakaopayRedirectType.FAIL)
                    .partnerOrderId(kakaoRedirectParameter.getPartnerOrderId())
                    .partnerUserId(kakaoRedirectParameter.getPartnerUserId()).build());
        } catch (Exception e) {

        }
        return new RedirectView(kakaoRedirectParameter.getRedirect());
    }
}

