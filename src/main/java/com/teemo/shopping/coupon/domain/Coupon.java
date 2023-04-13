package com.teemo.shopping.coupon.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.validator.CouponConstraint;
import com.teemo.shopping.game.domain.Game;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
        CouponMethod method, boolean canApplyToAll,  double amount, LocalDateTime expiredAt) {
            this.name = name;
            this.description = description;
            this.maxDiscountPrice = maxDiscountPrice;
            this.minDiscountPrice = minDiscountPrice;
            this.minFulfillPrice = minFulfillPrice;
            this.method = method;
            this.amount = amount;
            this.canApplyToAll = canApplyToAll;
            this.expiredAt = expiredAt;
    }
    @Override
    public boolean equals(Object obj) {
        Coupon target = (Coupon)obj;
        return target.getId().equals(this.getId());
    }
    @Column
    @NotNull
    private String name;

    @Column
    private String description;

    @Column
    private int maxDiscountPrice; // For Percent

    @Column
    private int minDiscountPrice; // For Percent

    @Column
    @Range(min = 0)
    private int minFulfillPrice; // For all

    @Enumerated
    @NotNull
    private CouponMethod method;

    /**
     * if (method == PERCENT) 0 ~ 100 [%]
     * else if(method == STATIC) 0 ~ [원]
     */
    @Column
    @Range(min = 0)
    private double amount;

    @Column
    private boolean canApplyToAll;
    @Column
    @NotNull
    private LocalDateTime expiredAt;

}

