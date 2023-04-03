package com.teemo.shopping.Order.service.payment_factory;

import com.teemo.shopping.Order.domain.CouponPayment;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.repository.PaymentRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@org.springframework.core.annotation.Order(200)
public class CouponPaymentFactory implements SingleProductPaymentFactory {

    @Autowired
    private PaymentRepository<CouponPayment> couponPaymentRepository;

    public PaymentMethod getTargetPaymentMethod() {
        return PaymentMethod.COUPON;
    }

    @Override
    public Optional<Payment> create(SingleGameProductContext context) {
        if (context.getCoupon().isEmpty()) {
            return Optional.empty();
        }
        Coupon coupon = context.getCoupon().orElseThrow();  // 타입 스크립트는 타입체크 후 그냥 가져가게 해주는데 ㅠ
        if (context.getAccount().getCoupons().contains(coupon)) { //account has coupon validate
            throw new RuntimeException();
        }
        if (coupon.getMinFulfillPrice()
            > context.getRemainPrice()) {//coupon minFulfillPrice validate
            throw new RuntimeException();
        }

        int couponPrice = 0;
        if (coupon.getMethod() == CouponMethod.PERCENT) {
            couponPrice = Math.max(coupon.getMinDiscountPrice(),
                Math.min(coupon.getMaxDiscountPrice(), (int) (
                    context.getRemainPrice() * coupon.getAmount())));
        } else if (coupon.getMethod() == CouponMethod.STATIC) {
            couponPrice = Math.max(0, (int) (context.getRemainPrice() - coupon.getAmount()));
        }
        CouponPayment couponPayment = CouponPayment.builder().coupon(coupon).status(
            PaymentStatus.SUCCESS).game(context.getGame()).build();
        couponPaymentRepository.save(couponPayment);
        context.setRemainPrice(context.getRemainPrice() - couponPrice);
        return Optional.of(couponPayment);
    }
}
