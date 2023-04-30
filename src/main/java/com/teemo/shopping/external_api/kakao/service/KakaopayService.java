package com.teemo.shopping.external_api.kakao.service;

import com.teemo.shopping.core.layer.ServiceLayer;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveResponse;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPICancelRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPICancelResponse;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIReadyRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIReadyResponse;
import com.teemo.shopping.external_api.kakao.dto.KakaopayApproveParameter;
import com.teemo.shopping.external_api.kakao.dto.KakaopayCancelParameter;
import com.teemo.shopping.external_api.kakao.dto.KakaopayReadyParameter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KakaopayService implements ServiceLayer {

    @Value("${kakao.admin-key}")
    private String KAKAO_ADMIN_KEY;

    //Todo: 테스트 작성
    public Mono<KakaopayAPIReadyResponse> readyKakaopay(
        KakaopayReadyParameter kakaopayReadyParameter) { // Parameter DTO로 변경

        String additionalQuery = "?" + StringUtils.join(
            kakaopayReadyParameter.getAdditionalQuery().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue()).toList(), "&");
        KakaopayAPIReadyRequest request = KakaopayAPIReadyRequest.builder()
            .cid(kakaopayReadyParameter.getCid())
            .partnerOrderId(kakaopayReadyParameter.getPartnerOrderId())
            .partnerUserId(kakaopayReadyParameter.getPartnerUserId())
            .itemName(kakaopayReadyParameter.getItemName())
            .quantity(kakaopayReadyParameter.getQuantity())
            .totalAmount(kakaopayReadyParameter.getAmount())
            .taxFreeAmount(0)
            .approvalUrl(kakaopayReadyParameter.getApprovalURL()
                + additionalQuery)
            .cancelUrl(kakaopayReadyParameter.getCancelURL()
                + additionalQuery)
            .failUrl(kakaopayReadyParameter.getFailURL()
                + additionalQuery)
            .build();
        return WebClient.create(
                "https://kapi.kakao.com/v1/payment/ready")
            .post()
            .header("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(request.toFormData())
            .retrieve()
            .bodyToMono(KakaopayAPIReadyResponse.class);
    }

    //Todo: 테스트 작성
    public Mono<KakaopayAPIApproveResponse> approveKakaopay(
        KakaopayApproveParameter kakaopayApproveParameter) {
        KakaopayAPIApproveRequest request = KakaopayAPIApproveRequest.builder()
            .pgToken(kakaopayApproveParameter.getPgToken())
            .cid(kakaopayApproveParameter.getCid())
            .tid(kakaopayApproveParameter.getTid())
            .partnerOrderId(kakaopayApproveParameter.getPartnerOrderId())
            .partnerUserId(kakaopayApproveParameter.getPartnerUserId())
            .build();

        return WebClient.create(
                "https://kapi.kakao.com/v1/payment/approve")
            .post()
            .header("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(request.toFormData())
            .retrieve()//todo: 에러처리 추가
            .bodyToMono(KakaopayAPIApproveResponse.class);
    }

    public Mono<KakaopayAPICancelResponse> cancelKakaopay(
        KakaopayCancelParameter parameter) {
        KakaopayAPICancelRequest request = KakaopayAPICancelRequest.builder()
            .tid(parameter.getTid())
            .cancelAmount(parameter.getAmount())
            .cid(parameter.getCid())
            .cancelTaxFreeAmount(0)
            .build();

        return WebClient.create(
                "https://kapi.kakao.com/v1/payment/cancel")
            .post()
            .header("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(request.toFormData())
            .retrieve()//todo: 에러처리 추가
            .bodyToMono(KakaopayAPICancelResponse.class);
    }
}
