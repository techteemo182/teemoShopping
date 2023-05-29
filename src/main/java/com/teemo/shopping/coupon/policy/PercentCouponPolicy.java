package com.teemo.shopping.coupon.policy;

import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.game.dto.GameCategoryDTO;
import com.teemo.shopping.game.dto.GameDTO;

import java.util.List;

public class PercentCouponPolicy extends CouponPolicy {

    public PercentCouponPolicy(CouponDTO coupon, List<CouponCondition> couponConditions) {
        super(coupon, couponConditions);
    }

    @Override
    public int getAppliedAmount(GameDTO game, int amount) {
        int appliedAmount = (int) (coupon.getAmount() / 100d * amount);
        appliedAmount = Math.min(Math.max(appliedAmount, coupon.getMinDiscountPrice()), coupon.getMaxDiscountPrice());
        return appliedAmount;
    }
}
