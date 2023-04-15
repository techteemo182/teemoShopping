package com.teemo.shopping.account.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderAddResponse {
    private final Long orderId;
    private final String redirect;
    private final List<Long> paymentIds;
    private final List<Long> pendingPaymentIds;
}
