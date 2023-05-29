package com.teemo.shopping.coupon.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.game.dto.GameCategoryDTO;
import com.teemo.shopping.game.dto.GameDTO;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

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

    private List<GameCategoryDTO> gameCategories;
    private List<GameDTO> games;

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
                .gameCategories(coupon.getCouponsGameCategories().stream().map(couponsGameCategories -> GameCategoryDTO.from(couponsGameCategories.getGameCategory())).toList())    // 무한 반복 양방향이라서
                .games(coupon.getCouponsGames().stream().map(couponsGames -> GameDTO.from(couponsGames.getGame())).toList())
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
