package com.teemo.shopping.coupon.policy;

import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.game.dto.GameCategoryDTO;
import com.teemo.shopping.game.dto.GameDTO;

import java.util.ArrayList;
import java.util.List;

public abstract class CouponPolicy {
    public CouponPolicy(CouponDTO coupon, List<CouponCondition> couponConditions) {
        this.coupon = coupon;
        this.couponConditions = couponConditions;
    }

    protected final CouponDTO coupon;
    protected List<CouponCondition> couponConditions;

    public boolean isApplicable(GameDTO game, int amount) {
        for (var couponCondition : couponConditions) {
            if (!couponCondition.checkCondition(game, amount)) return false;
        }
        return true;
    }
    public void checkApplicableElseThrow(GameDTO game, int amount) {
        for (var couponCondition : couponConditions) {
            couponCondition.checkConditionElseThrow(game, amount);
        }
    }

    public abstract int getAppliedAmount(GameDTO game, int amount);
}
