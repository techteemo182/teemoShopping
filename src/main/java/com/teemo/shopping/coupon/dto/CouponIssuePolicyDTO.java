package com.teemo.shopping.coupon.dto;

import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.CouponIssuePolicy;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
@Builder
public class CouponIssuePolicyDTO {

    private final Long id;
    private final Long couponId;
    @Range(min = 1)
    private final Integer amount; //발급 수량
    private final boolean isNewAccount;  // 새로운 유저
    private final boolean isFirstCome;    // 선착순
    private final Integer remainAmount;   //선착순 일때 남은 수량
    private final LocalDateTime startAt;  //시작
    private final LocalDateTime endAt;    //끝

    public static CouponIssuePolicyDTO from(CouponIssuePolicy couponIssuePolicy) {
        return CouponIssuePolicyDTO.builder()
            .id(couponIssuePolicy.getId())
            .couponId(couponIssuePolicy.getCoupon().getId())
            .amount(couponIssuePolicy.getAmount())
            .isNewAccount(couponIssuePolicy.isNewAccount())
            .isFirstCome(couponIssuePolicy.isFirstCome())
            .remainAmount(couponIssuePolicy.getRemainAmount())
            .startAt(couponIssuePolicy.getStartAt())
            .endAt(couponIssuePolicy.getEndAt())
            .build();
    }
}
