package com.teemo.shopping.external_api.kakao.dto;

import com.teemo.shopping.game.dto.GameDTO;
import java.util.Iterator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KakaopayReadyRequest {
    private String itemName;
    private String cid;
    private String partnerOrderId;
    private String partnerUserId;
    private Integer price;
}
