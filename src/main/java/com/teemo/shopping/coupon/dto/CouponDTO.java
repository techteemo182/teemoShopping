package com.teemo.shopping.coupon.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

// DTO Mirror 플러그인 찾아보기
// 옳은 아키텍쳐는 복사 붙여넣기를 하면 안되는것을
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CouponDTO {
    @JsonCreator
    @Builder
    protected CouponDTO(Long id, String name, String description, int maxDiscountPrice,
        int minDiscountPrice, int minFulfillPrice, CouponMethod method, boolean canApplyToAll,
        double amount, LocalDateTime expiredAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxDiscountPrice = maxDiscountPrice;
        this.minDiscountPrice = minDiscountPrice;
        this.minFulfillPrice = minFulfillPrice;
        this.method = method;
        this.canApplyToAll = canApplyToAll;
        this.amount = amount;
        this.expiredAt = expiredAt;
    }

    private final Long id;
    private final String name;
    private final String description;
    @Range(min = 0)
    @Nullable
    private final Integer maxDiscountPrice;
    @Range(min = 0)
    @Nullable
    private final Integer minDiscountPrice;
    @Range(min = 0)
    private final Integer minFulfillPrice;
    private final CouponMethod method;

    private final Boolean canApplyToAll;
    @Range(min = 0)
    private final Double amount;

    private final LocalDateTime expiredAt;

    public static CouponDTO from(Coupon coupon) {
        return CouponDTO.builder()
            .id(coupon.getId())
            .name(coupon.getName())
            .description(coupon.getDescription())
            .maxDiscountPrice(coupon.getMaxDiscountPrice())
            .minDiscountPrice(coupon.getMinDiscountPrice())
            .minFulfillPrice(coupon.getMinFulfillPrice())
            .method(coupon.getMethod())
            .amount(coupon.getAmount())
            .canApplyToAll(coupon.isCanApplyToAll())
            .build();
    }
    public Coupon to() {
        return Coupon.builder()
            .name(name)
            .description(description)
            .maxDiscountPrice(maxDiscountPrice)
            .minDiscountPrice(minDiscountPrice)
            .minFulfillPrice(minFulfillPrice)
            .method(method)
            .amount(amount)
            .canApplyToAll(canApplyToAll)
            .expiredAt(expiredAt)
            .build();
    }
}
