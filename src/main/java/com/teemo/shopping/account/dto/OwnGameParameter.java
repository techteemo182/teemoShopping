package com.teemo.shopping.account.dto;

import com.teemo.shopping.game.dto.GameDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OwnGameParameter {
    private final Long accountId;
    private final Long GameId;

}
