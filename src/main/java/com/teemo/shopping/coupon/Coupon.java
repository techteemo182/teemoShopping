package com.teemo.shopping.coupon;

import com.teemo.shopping.core.BaseEntity;
import com.teemo.shopping.coupon.validator.CouponConstraint;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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
    protected Coupon(double maxDiscountPrice, double minDiscountPrice, double minFulfillPrice,
        CouponMethod method, double amount) {
        this.maxDiscountPrice = maxDiscountPrice;
        this.minDiscountPrice = minDiscountPrice;
        this.minFulfillPrice = minFulfillPrice;
        this.method = method;
        this.amount = amount;
    }

    @Column
    private String name;

    @Column
    private String description;

    @Column
    @Range(min = 0)
    double maxDiscountPrice; // For Percent

    @Column
    @Range(min = 0)
    double minDiscountPrice; // For Percent

    @Column
    @Range(min = 0)
    double minFulfillPrice; // For all

    @Enumerated
    CouponMethod method;

    @Column
    @Range(min = 0)
    double amount;  // For all
}

