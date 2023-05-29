package com.teemo.shopping.coupon.policy;

import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.game.dto.GameDTO;

import java.util.List;

public class StaticCouponPolicy extends CouponPolicy {
    public StaticCouponPolicy(CouponDTO coupon, List<CouponCondition> couponConditions) {
        super(coupon, couponConditions);
    }

    @Override
    public int getAppliedAmount(GameDTO game, int amount) {
        return coupon.getAmount().intValue();
    }
}
