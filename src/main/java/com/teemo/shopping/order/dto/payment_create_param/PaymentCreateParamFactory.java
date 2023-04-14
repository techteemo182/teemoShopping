package com.teemo.shopping.order.dto.payment_create_param;

import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.order.domain.CouponPayment;
import com.teemo.shopping.order.domain.DiscountPayment;
import com.teemo.shopping.order.domain.KakaopayPayment;
import com.teemo.shopping.order.domain.PointPayment;
import com.teemo.shopping.order.dto.OrderCreateContext;
import java.util.Optional;

public class PaymentCreateParamFactory {
    public Optional<PaymentCreateParam> create(Class paymentClass,
        OrderCreateContext orderCreateContext) {
        if (paymentClass.equals(CouponPayment.class)) {
            Coupon coupon = orderCreateContext.getCoupon().orElse(null);

            return Optional.of(
                CouponPaymentCreateParam
                    .builder()
                    .couponId(coupon == null ? null : coupon.getId())
                    .gameId(orderCreateContext.getGame().getId())
                    .amount(orderCreateContext.getAmount())
                    .orderId(orderCreateContext.getOrder().getId())
                    .accountId(orderCreateContext.getAccount().getId())
                    .build()
            );
        } else if(paymentClass.equals(DiscountPayment.class)) {
            return Optional.of(
                DiscountPaymentCreateParam
                    .builder()
                    .gameId(orderCreateContext.getGame().getId())
                    .amount(orderCreateContext.getAmount())
                    .orderId(orderCreateContext.getOrder().getId())
                    .build()
            );
        } else if(paymentClass.equals(KakaopayPayment.class)) {
            return Optional.of(
                KakaopayPaymentCreateParam
                    .builder()
                    .amount(orderCreateContext.getAmount())
                    .orderId(orderCreateContext.getOrder().getId())
                    .itemName(orderCreateContext.getItemName())
                    .build()
            );
        } else if(paymentClass.equals(PointPayment.class)) {
            return Optional.of(
                PointPaymentCreateParam
                    .builder()
                    .amount(orderCreateContext.getAmount())
                    .availablePoint(orderCreateContext.getPoint())
                    .orderId(orderCreateContext.getOrder().getId())
                    .accountId(orderCreateContext.getAccount().getId())
                    .build()
            );
        }
        return Optional.empty();
    }
}
