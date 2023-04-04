package com.teemo.shopping.coupon.domain;

import com.teemo.shopping.core.BaseEntity;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.validator.CouponConstraint;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AttributeOverride(name = "id", column = @Column(name = "coupon_id"))
@CouponConstraint
public class Coupon extends BaseEntity {

    @Builder
    protected Coupon(String name, String description, int maxDiscountPrice, int minDiscountPrice, int minFulfillPrice,
        CouponMethod method, double amount, LocalTime expiredAt) {
            this.name = name;
            this.description = description;
            this.maxDiscountPrice = maxDiscountPrice;
            this.minDiscountPrice = minDiscountPrice;
            this.minFulfillPrice = minFulfillPrice;
            this.method = method;
            this.amount = amount;
            this.expiredAt = expiredAt;
    }

    @Column
    private String name;

    @Column
    private String description;

    @Column
    @Range(min = 0)
    int maxDiscountPrice; // For Percent

    @Column
    @Range(min = 0)
    int minDiscountPrice; // For Percent

    @Column
    @Range(min = 0)
    int minFulfillPrice; // For all

    @Enumerated
    CouponMethod method;

    @Column
    @Range(min = 0)
    double amount;  // For all

    @Column
    LocalTime expiredAt;

}

