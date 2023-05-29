package com.teemo.shopping.coupon.policy;

import com.teemo.shopping.game.dto.GameDTO;

public class MinAmountCouponCondition extends CouponCondition {
    public MinAmountCouponCondition(int minAmount) {
        this.minAmount = minAmount;
    }
    private final int minAmount;

    @Override
    protected String getFalseMessage() {
        return "최소 충족 금액을 만족하지 못하였습니다.";
    }

    @Override
    public boolean checkCondition(GameDTO game, int amount) {
        return minAmount <= amount;
    }
}
