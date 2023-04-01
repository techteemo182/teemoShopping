package com.teemo.shopping.external_api.kakao.dto;

import com.teemo.shopping.game.dto.GameDTO;
import java.util.Iterator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
//그냥 Lombok 쓰지 말까 Lombok 자체가 확장성이 없다.

public class KakaopayReadyRequest {
    private Iterator<GameDTO> gameDTOs;
    private Integer totalPrice;
}
