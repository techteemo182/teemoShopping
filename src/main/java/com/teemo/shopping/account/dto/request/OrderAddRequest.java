package com.teemo.shopping.account.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.order.enums.PaymentMethods;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor(onConstructor_ = {@JsonCreator})
@Getter
public class OrderAddRequest {
    private Integer point;
    private PaymentMethods paymentMethod;
    private Optional<String> redirect;
    private List<GamePaymentInformation> gameInfos;
}

