package com.teemo.shopping.coupon.validator;

import com.teemo.shopping.coupon.Coupon;
import com.teemo.shopping.coupon.CouponMethod;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class CouponValidator implements ConstraintValidator<CouponConstraint, Coupon> {

    @Override
    public boolean isValid(Coupon value, ConstraintValidatorContext context) {
        if (value.getMethod() == CouponMethod.PERCENT) {
            if(value.getAmount() > 100) return false;
            if(value.getMinDiscountPrice() > value.getMaxDiscountPrice()) return false;
        }
        return true;
    }


}