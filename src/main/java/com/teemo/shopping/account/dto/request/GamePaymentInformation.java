package com.teemo.shopping.account.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor(onConstructor_ = {@JsonCreator})
@Getter
public class GamePaymentInformation {
    private Long gameId;
    private Optional<Long> couponId;
}
