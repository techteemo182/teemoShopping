package com.teemo.shopping.coupon.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE,
})
@Constraint(validatedBy = CouponValidator.class)
public @interface CouponConstraint {

    String message() default "Invalid Coupon";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}