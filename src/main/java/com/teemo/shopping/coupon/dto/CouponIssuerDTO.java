package com.teemo.shopping.coupon.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.coupon.domain.CouponIssuer;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CouponIssuerDTO {
    protected CouponIssuerDTO(Long id, Long couponId, Integer amount, boolean isNewAccount,
        boolean isFirstCome, Integer remainAmount, LocalDateTime startAt, LocalDateTime endAt) {
        this.id = id;
        this.couponId = couponId;
        this.amount = amount;
        this.isNewAccount = isNewAccount;
        this.isFirstCome = isFirstCome;
        this.remainAmount = remainAmount;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    private Long id;
    private Long couponId;
    @Range(min = 1)
    private Integer amount; //발급 수량
    private boolean isNewAccount;  // 새로운 유저
    private boolean isFirstCome;    // 선착순
    private Integer remainAmount;   //선착순 일때 남은 수량
    private LocalDateTime startAt;  //시작
    private LocalDateTime endAt;    //끝

    public static CouponIssuerDTO from(CouponIssuer couponIssuer) {
        return CouponIssuerDTO.builder()
            .id(couponIssuer.getId())
            .couponId(couponIssuer.getCoupon().getId())
            .amount(couponIssuer.getAmount())
            .isNewAccount(couponIssuer.isNewAccount())
            .isFirstCome(couponIssuer.isFirstCome())
            .remainAmount(couponIssuer.getRemainAmount())
            .startAt(couponIssuer.getStartAt())
            .endAt(couponIssuer.getEndAt())
            .build();
    }
}
