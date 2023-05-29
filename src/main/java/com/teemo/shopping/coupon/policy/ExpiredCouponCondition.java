package com.teemo.shopping.coupon.policy;

import com.teemo.shopping.game.dto.GameDTO;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ExpiredCouponCondition extends CouponCondition {
    public ExpiredCouponCondition(LocalDateTime expire) {
        this.expire = expire;
    }

    private final LocalDateTime expire;
    @Override
    protected String getFalseMessage() {
        return "쿠폰 기간 만료";
    }

    @Override
    boolean checkCondition(GameDTO game, int amount) {
        return LocalDateTime.now(ZoneOffset.UTC).isBefore(expire);
    }
}
