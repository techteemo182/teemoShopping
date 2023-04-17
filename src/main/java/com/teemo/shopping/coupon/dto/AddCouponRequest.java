package com.teemo.shopping.coupon.dto;

import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AddCouponRequest {
    @NotNull
    private final String name;
    @NotNull
    private final String description;
    @Range(min = 0)
    @Nullable
    private final Integer maxDiscountPrice;
    @Range(min = 0)
    @Nullable
    private final Integer minDiscountPrice;
    @Range(min = 0)
    private final Integer minFulfillPrice;
    @NotNull
    private final CouponMethod method;
    @NotNull
    private final Boolean canApplyToAll;
    @Range(min = 0)
    @NotNull
    private final Double amount;
    @NotNull
    private final LocalDateTime expiredAt;
}
