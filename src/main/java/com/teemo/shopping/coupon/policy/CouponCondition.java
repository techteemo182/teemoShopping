package com.teemo.shopping.coupon.policy;

import com.teemo.shopping.game.dto.GameDTO;

public abstract class CouponCondition {
    abstract protected String getFalseMessage();
    abstract boolean checkCondition(GameDTO game, int amount);

    void checkConditionElseThrow(GameDTO game, int amount) {
        if(!checkCondition(game, amount)) {
            throw new IllegalStateException(getFalseMessage());
        }
    }
}
