package com.teemo.shopping.coupon;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.teemo.shopping.Main;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.repository.AccountsCouponsRepository;
import com.teemo.shopping.account.service.AccountAuthenticationService;
import com.teemo.shopping.account.service.AccountService;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.CouponIssuePolicy;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.coupon.dto.CouponIssuePolicyDTO;
import com.teemo.shopping.coupon.repository.CouponIssuePolicyRepository;
import com.teemo.shopping.coupon.service.CouponIssuePolicyService;
import com.teemo.shopping.coupon.service.CouponService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootTest(classes = Main.class)
public class CouponTest {
    @Test
    void couponTest() {

        Coupon normalCoupon1 = Coupon.builder()
            .minFulfillPrice(1000)
            .amount(3000)
            .name("특전 3000")
            .method(CouponMethod.STATIC)
            .build();
        Coupon normalCoupon2 = Coupon.builder()
            .maxDiscountPrice(1000)
            .minDiscountPrice(0)
            .minFulfillPrice(1000)
            .name("특전 3000")
            .amount(50)
            .method(CouponMethod.PERCENT)
            .build();
        //최대 세일 금액이 최소 세일 금액보다 작음
        Coupon abnormalCoupon1 = Coupon.builder()
            .maxDiscountPrice(1000)
            .minDiscountPrice(5000)
            .minFulfillPrice(1000)
            .name("특전 3000")
            .amount(50)
            .method(CouponMethod.PERCENT)
            .build();

        //120% 세일
        Coupon abnormalCoupon2 = Coupon.builder()
            .maxDiscountPrice(1000)
            .minDiscountPrice(0)
            .minFulfillPrice(1000)
            .name("특전 3000")
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

    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponIssuePolicyService couponIssuePolicyService;
    @Autowired
    private AccountAuthenticationService accountAuthenticationService;
    @Autowired
    private AccountService accountService;
    @Test
    void couponIssue() throws Exception {

        Long accountId = accountAuthenticationService.register("teemo", "teemo");
        Long couponId = couponService.add(CouponDTO.builder()
                .name("마인크래프트 출시기념")
                .canApplyToAll(true)
                .method(CouponMethod.STATIC)
                .minFulfillPrice(10000)
                .amount(3000)
                .build());
        Long couponIssuePolicyId = couponIssuePolicyService.add(CouponIssuePolicyDTO.builder()
                .amount(1)
                .couponId(couponId)
                .startAt(LocalDateTime.now())
                .endAt((LocalDateTime.MAX))
                .isFirstCome(false)
                .isNewAccount(false)
            .build());
        couponIssuePolicyService.issueCoupon(accountId, couponIssuePolicyId);


        var a= accountService.getCoupons(accountId);


    }
}
