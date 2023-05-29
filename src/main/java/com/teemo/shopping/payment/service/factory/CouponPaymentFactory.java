package com.teemo.shopping.payment.service.factory;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.coupon.policy.CouponPolicy;
import com.teemo.shopping.coupon.policy.factory.CouponPolicyFactory;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.payment.domain.CouponPayment;
import com.teemo.shopping.payment.domain.Payment;

public class CouponPaymentFactory implements PaymentFactory<OrderContext>{
    @Override
    public Payment create(OrderContext orderContext) {
        Account account = orderContext.getAccount().get();
        Coupon coupon = orderContext.getCoupon().get();
        Game game = orderContext.getGame().get();
        CouponPolicyFactory couponPolicyFactory = new CouponPolicyFactory();
        CouponPolicy couponPolicy = couponPolicyFactory.create(CouponDTO.from(coupon));
        couponPolicy.checkApplicableElseThrow(GameDTO.from(game), orderContext.getRemainAmount());
        int amount = couponPolicy.getAppliedAmount(GameDTO.from(game), orderContext.getRemainAmount());
        return CouponPayment.builder().coupon(coupon).account(account)
                .amount(amount).game(game).build();
    }
}
