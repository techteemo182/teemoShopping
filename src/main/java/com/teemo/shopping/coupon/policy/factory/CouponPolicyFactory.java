package com.teemo.shopping.coupon.policy.factory;

import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.coupon.policy.*;
import com.teemo.shopping.game.dto.GameCategoryDTO;
import com.teemo.shopping.game.dto.GameDTO;

import java.util.ArrayList;
import java.util.List;

public class CouponPolicyFactory {  // 구체 클래스
    public CouponPolicy create(CouponDTO coupon) {
        List<CouponCondition> couponConditions = new ArrayList<>();
        couponConditions.add(new MinAmountCouponCondition(coupon.getMinFulfillPrice()));
        couponConditions.add(new GameOrGameCouponCondition(coupon.getGames().stream().map(GameDTO::getId).toList(), coupon.getGameCategories().stream().map(GameCategoryDTO::getId).toList()));
        couponConditions.add(new ExpiredCouponCondition(coupon.getExpiredAt()));
        if(coupon.getMethod().equals(CouponMethod.STATIC)) {
            return new StaticCouponPolicy(coupon, couponConditions);
        } else if(coupon.getMethod().equals(CouponMethod.PERCENT)) {
            return new PercentCouponPolicy(coupon, couponConditions);
        }
        throw new IllegalStateException("변환 불가능한 객체");
    }
}
