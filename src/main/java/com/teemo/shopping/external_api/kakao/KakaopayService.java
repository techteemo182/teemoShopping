package com.teemo.shopping.external_api.kakao;

import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveResponse;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIReadyRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIReadyResponse;
import com.teemo.shopping.external_api.kakao.dto.KakaopayApproveRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayReadyRequest;
import com.teemo.shopping.game.dto.GameDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaopayService {

    @Value("${key.kakao.admin-key}")
    private String KAKAO_ADMIN_KEY;

    //Todo: 테스트 작성
    public void prepareKakaopay(KakaopayReadyRequest kakaopayReadyRequest) { // Parameter DTO로 변경
        StringBuffer itemNameBuffer = new StringBuffer();
        String itemName;
        List<GameDTO> gameDTOs = new ArrayList<>();
        while(kakaopayReadyRequest.getGameDTOs().hasNext()) {
            gameDTOs.add(kakaopayReadyRequest.getGameDTOs().next());
        }
        for (var gameDTO : gameDTOs) {
            itemNameBuffer.append(gameDTO.getName() + ",");
        }
        itemNameBuffer.deleteCharAt(itemNameBuffer.length() - 1);   // 마지막 , 지우기
        itemName = itemNameBuffer.toString();
        KakaopayAPIReadyRequest request = KakaopayAPIReadyRequest.builder()
            .cid("TC0ONETIME")
            .partnerOrderId(UUID.randomUUID().toString())
            .partnerUserId("main")
            .itemName(itemName)
            .quantity(1)
            .totalAmount(kakaopayReadyRequest.getTotalPrice())
            .taxFreeAmount(0)
            .approvalUrl("http://teemohouse.techteemo.store:8080/kakao/success")
            .cancelUrl("http://teemohouse.techteemo.store:8080/kakao/cancle")
            .failUrl("http://teemohouse.techteemo.store:8080/kakao/fail")
            .build();
        KakaopayAPIReadyResponse kakaopayAPIReadyResponse = WebClient.create(
                "https://kapi.kakao.com/v1/payment/ready")
            .post()
            .header("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(request.toFormData())
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                clientResponse.bodyToMono(String.class).subscribe((body) -> {
                    //Todo:
                });
                return null;
            })
            .bodyToMono(KakaopayAPIReadyResponse.class)
            .block();
    }

    //Todo: 테스트 작성
    public void approveKakaopay(KakaopayApproveRequest kakaopayApproveRequest) { // Parameter DTO로 변경
        StringBuffer itemNameBuffer = new StringBuffer();
        String itemName;
        itemNameBuffer.deleteCharAt(itemNameBuffer.length() - 1);   // 마지막 , 지우기
        itemName = itemNameBuffer.toString();
        KakaopayAPIApproveRequest request = KakaopayAPIApproveRequest.builder()
            .pgToken(kakaopayApproveRequest.getPgToken())
            .cid("TC0ONETIME")
            .tid(kakaopayApproveRequest.getTid())
            .partnerOrderId(kakaopayApproveRequest.getPartnerOrderId())
            .partnerUserId(kakaopayApproveRequest.getPartnerUserId())
            .build();

            KakaopayAPIApproveResponse kakaopayAPIApproveResponse = WebClient.create(
                "https://kapi.kakao.com/v1/payment/approve")
            .post()
            .header("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(request.toFormData())
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                clientResponse.bodyToMono(String.class).subscribe((body) -> {
                    //Todo:

                });
                return null;
            })
            .bodyToMono(KakaopayAPIApproveResponse.class)
            .block();
    }
}
