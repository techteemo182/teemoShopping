package com.teemo.shopping.account.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.order.enums.PaymentMethod;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderAddRequest {

    @JsonCreator
    public OrderAddRequest(Integer point, List<PaymentMethod> methods, List<Long> gameIds,
        Map<Long, Long> gameCouponIdMap, String redirect) {
        this.point = point;
        this.methods = methods;
        this.gameIds = gameIds;
        this.gameCouponIdMap = gameCouponIdMap;
        this.redirect = redirect;
    }

    private final Integer point;
    private final List<PaymentMethod> methods;
    private final List<Long> gameIds;
    private final Map<Long, Long> gameCouponIdMap;
    private final String redirect;
}

