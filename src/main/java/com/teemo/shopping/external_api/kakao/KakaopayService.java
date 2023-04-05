package com.teemo.shopping.external_api.kakao;

import com.teemo.shopping.core.layer.ServiceLayer;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveResponse;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIReadyRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIReadyResponse;
import com.teemo.shopping.external_api.kakao.dto.KakaopayApproveRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayReadyRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KakaopayService implements ServiceLayer {

    @Value("${key.kakao.admin-key}")
    private String KAKAO_ADMIN_KEY;

    //Todo: 테스트 작성
    public Mono<KakaopayAPIReadyResponse> readyKakaopay(
        KakaopayReadyRequest kakaopayReadyRequest) { // Parameter DTO로 변경
        String partnerOrderId = kakaopayReadyRequest.getPartnerOrderId();
        String partnerUserId = kakaopayReadyRequest.getPartnerUserId();
        String cid = kakaopayReadyRequest.getCid();

        KakaopayAPIReadyRequest request = KakaopayAPIReadyRequest.builder()
            .cid(cid)
            .partnerOrderId(partnerOrderId)
            .partnerUserId(partnerUserId)
            .itemName(kakaopayReadyRequest.getItemName())
            .quantity(1)
            .totalAmount(kakaopayReadyRequest.getPrice())
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
        KakaopayApproveRequest kakaopayApproveRequest) {
        KakaopayAPIApproveRequest request = KakaopayAPIApproveRequest.builder()
            .pgToken(kakaopayApproveRequest.getPgToken())
            .cid(kakaopayApproveRequest.getCid())
            .tid(kakaopayApproveRequest.getTid())
            .partnerOrderId(kakaopayApproveRequest.getPartnerOrderId())
            .partnerUserId(kakaopayApproveRequest.getPartnerUserId())
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
}
