package com.teemo.shopping.coupon.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

// DTO Mirror 플러그인 찾아보기
// 옳은 아키텍쳐는 복사 붙여넣기를 하면 안되는것을
@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CouponDTO {

    private Long id;
    private String name;
    private String description;
    @Range(min = 0)
    @Nullable
    private Integer maxDiscountPrice;
    @Range(min = 0)
    @Nullable
    private Integer minDiscountPrice;
    @Range(min = 0)
    private Integer minFulfillPrice;
    private CouponMethod method;

    @NotNull
    private Boolean canApplyToAll;
    @Range(min = 0)
    private Double amount;

    private LocalDateTime expiredAt;

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
