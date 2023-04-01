package com.teemo.shopping.external_api.kakao;

import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApprovalResponse;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIApproveRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIPrepareRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayAPIPrepareResponse;
import com.teemo.shopping.external_api.kakao.dto.KakaopayApprovalRequest;
import com.teemo.shopping.external_api.kakao.dto.KakaopayPrepareRequest;
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
    public void kakaopayPrepare(KakaopayPrepareRequest kakaopayPrepareRequest) { // Parameter DTO로 변경
        StringBuffer itemNameBuffer = new StringBuffer();
        String itemName;
        List<GameDTO> gameDTOs = new ArrayList<>();
        while(kakaopayPrepareRequest.getGameDTOs().hasNext()) {
            gameDTOs.add(kakaopayPrepareRequest.getGameDTOs().next());
        }
        for (var gameDTO : gameDTOs) {
            itemNameBuffer.append(gameDTO.getName() + ",");
        }
        itemNameBuffer.deleteCharAt(itemNameBuffer.length() - 1);   // 마지막 , 지우기
        itemName = itemNameBuffer.toString();
        KakaopayAPIPrepareRequest request = KakaopayAPIPrepareRequest.builder()
            .cid("TC0ONETIME")
            .partnerOrderId(UUID.randomUUID().toString())
            .partnerUserId("main")
            .itemName(itemName)
            .quantity(1)
            .totalAmount(kakaopayPrepareRequest.getTotalPrice())
            .taxFreeAmount(0)
            .approvalUrl("http://teemohouse.techteemo.store:8080/kakao/success")
            .cancelUrl("http://teemohouse.techteemo.store:8080/kakao/cancle")
            .failUrl("http://teemohouse.techteemo.store:8080/kakao/fail")
            .build();
        KakaopayAPIPrepareResponse kakaopayAPIPrepareResponse = WebClient.create(
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
            .bodyToMono(KakaopayAPIPrepareResponse.class)
            .block();
    }

    //Todo: 테스트 작성
    public void kakaopayApproval(KakaopayApprovalRequest kakaopayApprovalRequest) { // Parameter DTO로 변경
        StringBuffer itemNameBuffer = new StringBuffer();
        String itemName;
        itemNameBuffer.deleteCharAt(itemNameBuffer.length() - 1);   // 마지막 , 지우기
        itemName = itemNameBuffer.toString();
        KakaopayAPIApproveRequest request = KakaopayAPIApproveRequest.builder()
            .pgToken(kakaopayApprovalRequest.getPgToken())
            .cid("TC0ONETIME")
            .tid(kakaopayApprovalRequest.getTid())
            .partnerOrderId(kakaopayApprovalRequest.getPartnerOrderId())
            .partnerUserId(kakaopayApprovalRequest.getPartnerUserId())
            .build();

            KakaopayAPIApprovalResponse kakaopayAPIApprovalResponse = WebClient.create(
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
            .bodyToMono(KakaopayAPIApprovalResponse.class)
            .block();
    }
}
