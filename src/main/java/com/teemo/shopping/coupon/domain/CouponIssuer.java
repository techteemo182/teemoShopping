package com.teemo.shopping.coupon.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@Getter
@Entity
@AttributeOverride(name = "id", column = @Column(name = "couponIssuePolicies_id"))
@Table(
    name = "couponsIssuePolicies"
)
public class CouponIssuer extends BaseEntity {

    @Builder
    public CouponIssuer(Coupon coupon, Integer amount, boolean isNewAccount,
        boolean isFirstCome,
        Integer remainAmount, LocalDateTime startAt, LocalDateTime endAt) {
        this.coupon = coupon;
        this.amount = amount;
        this.isNewAccount = isNewAccount;
        this.isFirstCome = isFirstCome;
        this.remainAmount = remainAmount;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    @ManyToOne
    private Coupon coupon;
    @Column
    private Integer amount; //발급 수량
    @Column
    private boolean isNewAccount;  // 새로운 유저
    @Column
    private boolean isFirstCome;    // 선착순
    @Column
    private Integer remainAmount;   //선착순 일때 남은 수량
    @Column
    private LocalDateTime startAt;  //시작
    @Column
    private LocalDateTime endAt;    //끝

    public void updateRemainAmount(int remainAmount) {
        this.remainAmount = remainAmount;
    }
}
