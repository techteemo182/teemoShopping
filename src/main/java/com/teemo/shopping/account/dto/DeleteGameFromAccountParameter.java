package com.teemo.shopping.account.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteGameFromAccountParameter {
    private final Long accountId;
    private final Long GameId;

}
