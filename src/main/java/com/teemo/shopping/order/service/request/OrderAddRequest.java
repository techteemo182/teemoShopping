package com.teemo.shopping.order.service.request;

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
    protected OrderAddRequest(Integer point, List<PaymentMethod> methods, List<Long> gameIds,
        Map<Long, Long> gameCouponIdMap) {
        this.point = point;
        this.methods = methods;
        this.gameIds = gameIds;
        this.gameCouponIdMap = gameCouponIdMap;
    }

    private final Integer point;
    private final List<PaymentMethod> methods;
    private final List<Long> gameIds;
    private final Map<Long, Long> gameCouponIdMap;
}

