package com.teemo.shopping.coupon.domain;

import com.teemo.shopping.core.entity.BaseEntity;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.validator.CouponConstraint;
import com.teemo.shopping.game.domain.Game;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyDiscriminator;
import org.hibernate.annotations.AnyDiscriminatorValue;
import org.hibernate.annotations.AnyKeyJavaClass;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AttributeOverride(name = "id", column = @Column(name = "coupon_id"))
@CouponConstraint
@Table(
    name = "coupons",
    indexes = {
        @Index(columnList = "name")
    }
)
public class Coupon extends BaseEntity {

    @Builder
    protected Coupon(String name, String description, Integer maxDiscountPrice, Integer minDiscountPrice, Integer minFulfillPrice,
        CouponMethod method, boolean canApplyToAll,  Double amount, LocalDateTime expiredAt) {
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
    private Integer maxDiscountPrice; // For Percent

    @Column
    private Integer minDiscountPrice; // For Percent

    @Column
    @Range(min = 0)
    private Integer minFulfillPrice; // For all

    @Enumerated
    @NotNull
    private CouponMethod method;        // todo: inherited table로 전환

    /**
     * if (method == PERCENT) 0 ~ 100 [%]
     * else if(method == STATIC) 0 ~ [원]
     */
    @Range(min = 0)
    @Column
    private Double amount;

    @Column
    private boolean canApplyToAll;
    @Column
    @NotNull
    private LocalDateTime expiredAt;

    @OneToMany(mappedBy = "coupon", fetch = FetchType.LAZY)
    private List<CouponsGames> couponsGames = new ArrayList<>();
    @OneToMany(mappedBy = "coupon", fetch = FetchType.LAZY)
    private List<CouponsGameCategories> couponsGameCategories = new ArrayList<>();
}

