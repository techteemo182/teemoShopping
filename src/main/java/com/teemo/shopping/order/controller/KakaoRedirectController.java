package com.teemo.shopping.order.controller;

import com.teemo.shopping.external_api.kakao.dto.KakaoRedirectParameter;
import com.teemo.shopping.order.enums.KakaopayRedirectStates;
import com.teemo.shopping.order.service.payment.kakaopay_service.KakaopayPaymentOnRedirectService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/kakao")
public class KakaoRedirectController {

    @Autowired
    private KakaopayPaymentOnRedirectService kakaopayPaymentOnRedirectService;

    @Operation(operationId = "카카오페이 리다이렉트 성공", summary = "카카오페이 리다이렉트 성공", tags = {"카카오 리다이렉트"})
    @GetMapping("/success")
    public RedirectView success(KakaoRedirectParameter kakaoRedirectParameter) {

        try {
            kakaopayPaymentOnRedirectService.onKakaopayRedirect(kakaoRedirectParameter.getPaymentId(),
                kakaoRedirectParameter.getPgToken(), kakaoRedirectParameter.getRedirectSecret(),
                KakaopayRedirectStates.SUCCESS);
        } catch (Exception e) {

        }
        return new RedirectView(kakaoRedirectParameter.getRedirect());
    }

    @Operation(operationId = "카카오페이 리다이렉트 취소", summary = "카카오페이 리다이렉트 취소", tags = {"카카오 리다이렉트"})
    @GetMapping("/cancel")
    public RedirectView cancel(KakaoRedirectParameter kakaoRedirectParameter) {
        try {
            kakaopayPaymentOnRedirectService.onKakaopayRedirect(kakaoRedirectParameter.getPaymentId(),
                kakaoRedirectParameter.getPgToken(), kakaoRedirectParameter.getRedirectSecret(),
                KakaopayRedirectStates.CANCEL);
        } catch (Exception e) {

        }
        return new RedirectView(kakaoRedirectParameter.getRedirect());
    }

    @Operation(operationId = "카카오페이 리다이렉트 실패", summary = "카카오페이 리다이렉트 실패", tags = {"카카오 리다이렉트"})
    @GetMapping("/fail")
    public RedirectView fail(KakaoRedirectParameter kakaoRedirectParameter) {
        try {
            kakaopayPaymentOnRedirectService.onKakaopayRedirect(kakaoRedirectParameter.getPaymentId(),
                kakaoRedirectParameter.getPgToken(), kakaoRedirectParameter.getRedirectSecret(),
                KakaopayRedirectStates.FAIL);
        } catch (Exception e) {

        }
        return new RedirectView(kakaoRedirectParameter.getRedirect());
    }
}

