package com.teemo.shopping.coupon.validator;

import com.teemo.shopping.coupon.Coupon;
import com.teemo.shopping.coupon.CouponMethod;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class CouponValidator implements ConstraintValidator<CouponCheck, Coupon> {

    @Override
    public boolean isValid(Coupon value, ConstraintValidatorContext context) {
        if (value.getMethod() == CouponMethod.PERCENT && value.getAmount() > 100) {
            return false;
        }
        return value.getMinDiscountPrice() < value.getMaxDiscountPrice();
    }
}