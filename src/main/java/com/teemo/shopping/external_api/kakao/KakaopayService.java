package com.teemo.shopping.external_api.kakao;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KakaopayService implements ServiceLayer {

    @Value("${key.kakao.admin-key}")
    private String KAKAO_ADMIN_KEY;
    @Value("${key.kakao.cid}")
    private String cid;
    @Value("${key.kakao.partner-user-id}")
    private String partnerUserId;
    //Todo: 테스트 작성
    public Mono<KakaopayAPIReadyResponse> readyKakaopay(
        KakaopayReadyParameter kakaopayReadyParameter) { // Parameter DTO로 변경
        String partnerOrderId = kakaopayReadyParameter.getPartnerOrderId();

        KakaopayAPIReadyRequest request = KakaopayAPIReadyRequest.builder()
            .cid(cid)
            .partnerOrderId(partnerOrderId)
            .partnerUserId(partnerUserId)
            .itemName(kakaopayReadyParameter.getItemName())
            .quantity(1)
            .totalAmount(kakaopayReadyParameter.getPrice())
            .taxFreeAmount(0)
            .approvalUrl("http://teemohouse.techteemo.store:8080/kakao/success?partner_order_id=" + partnerOrderId + "&partner_user_id=" + partnerUserId)       // URL SSL 암호화 못믿으면 JWT 토큰 사용
            .cancelUrl("http://teemohouse.techteemo.store:8080/kakao/cancle?partner_order_id=" + partnerOrderId + "&partner_user_id=" + partnerUserId)
            .failUrl("http://teemohouse.techteemo.store:8080/kakao/fail?partner_order_id=" + partnerOrderId + "&partner_user_id=" + partnerUserId)
            .build();
        return WebClient.create(
                "https://kapi.kakao.com/v1/payment/ready")
            .post()
            .header("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(request.toFormData())
            .retrieve()     //todo: 에러처리 추가
            .bodyToMono(KakaopayAPIReadyResponse.class);
    }

    //Todo: 테스트 작성
    public Mono<KakaopayAPIApproveResponse> approveKakaopay(
        KakaopayApproveParameter kakaopayApproveParameter) {
        KakaopayAPIApproveRequest request = KakaopayAPIApproveRequest.builder()
            .pgToken(kakaopayApproveParameter.getPgToken())
            .cid(cid)
            .tid(kakaopayApproveParameter.getTid())
            .partnerOrderId(kakaopayApproveParameter.getPartnerOrderId())
            .partnerUserId(partnerUserId)
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
            .cancelAmount(parameter.getPrice())
            .cid(cid)
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
