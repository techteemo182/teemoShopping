package com.example.demo.coupon;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.teemo.shopping.Main;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootTest(classes = Main.class)
public class CouponTest {
    @Autowired
    private LocalValidatorFactoryBean localValidatorFactoryBean;
    @Test
    void couponValidationTest() {

        Coupon normalCoupon1 = Coupon.builder()
            .minFulfillPrice(1000)
            .amount(3000)
            .method(CouponMethod.STATIC)
            .build();

        Coupon normalCoupon2 = Coupon.builder()
            .maxDiscountPrice(1000)
            .minDiscountPrice(0)
            .minFulfillPrice(1000)
            .amount(50)
            .method(CouponMethod.PERCENT)
            .build();

        //최대 세일 금액이 최소 세일 금액보다 작음
        Coupon abnormalCoupon1 = Coupon.builder()
            .maxDiscountPrice(1000)
            .minDiscountPrice(5000)
            .minFulfillPrice(1000)
            .amount(50)
            .method(CouponMethod.PERCENT)
            .build();

        //120% 세일
        Coupon abnormalCoupon2 = Coupon.builder()
            .maxDiscountPrice(1000)
            .minDiscountPrice(0)
            .minFulfillPrice(1000)
            .amount(120)
            .method(CouponMethod.PERCENT)
            .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        assertTrue(validator.validate(normalCoupon1).isEmpty());
        assertTrue(validator.validate(normalCoupon2).isEmpty());
        assertFalse(validator.validate(abnormalCoupon1).isEmpty());
        assertFalse(validator.validate(abnormalCoupon2).isEmpty());

    }
}
