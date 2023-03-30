package com.teemo.shopping.coupon;

import com.teemo.shopping.core.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AttributeOverride(name = "id", column = @Column(name = "coupon_id"))
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
    @Range(min = 0)
    double maxDiscountPrice;

    @Column
    @Range(min = 0)
    double minDiscountPrice;

    @Column
    @Range(min = 0)
    double minFulfillPrice;

    @Enumerated
    CouponMethod method;

    @Column
    @Range(min = 0)
    double amount;
}

