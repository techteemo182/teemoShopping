package com.teemo.shopping.coupon.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AddCouponRequest {

    @NotNull
    private String name;
    @NotNull
    private String description;
    @Range(min = 0)
    @Nullable
    private Integer maxDiscountPrice;
    @Range(min = 0)
    @Nullable
    private Integer minDiscountPrice;
    @Range(min = 0)
    private Integer minFulfillPrice;
    @NotNull
    private CouponMethod method;
    @NotNull
    private Boolean canApplyToAll;
    @Range(min = 0)
    @NotNull
    private Double amount;
    @NotNull
    private LocalDateTime expiredAt;
}
