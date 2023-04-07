package com.teemo.shopping.Order.service;

import com.teemo.shopping.Order.domain.CouponPayment;
import com.teemo.shopping.Order.domain.Order;
import com.teemo.shopping.Order.domain.Payment;
import com.teemo.shopping.Order.domain.enums.PaymentMethod;
import com.teemo.shopping.Order.domain.enums.PaymentStatus;
import com.teemo.shopping.Order.dto.PaymentRefundParameter;
import com.teemo.shopping.Order.dto.PaymentCreateContext;
import com.teemo.shopping.Order.repository.PaymentRepository;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsCoupons;
import com.teemo.shopping.account.repository.AccountsCouponsRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.game.domain.Game;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("forOneProduct")
@org.springframework.core.annotation.Order(200)
public class CouponPaymentService extends PaymentService {

    @Autowired
    private PaymentRepository<CouponPayment> couponPaymentRepository;

    @Autowired
    private AccountsCouponsRepository accountsCouponsRepository;

    @Override
    public Optional<Payment> create(PaymentCreateContext context) throws RuntimeException {
        Game game = context.getGame();
        Coupon coupon = context.getCoupon().orElse(null);
        Account account = context.getAccount();
        com.teemo.shopping.Order.domain.Order order = context.getOrder();
        if (coupon == null) {
            return Optional.empty();
        }
        if (coupon.getMinFulfillPrice()
            > context.getRemainPrice()) { //쿠폰 최소 금액 충족 여부
            throw new RuntimeException();
        }

        AccountsCoupons accountsCoupons = accountsCouponsRepository.findFirstByAccountAndCoupon(account, coupon).orElseThrow(RuntimeException::new);
        int couponPrice = 0;
        if (coupon.getMethod() == CouponMethod.PERCENT) {
            couponPrice = Math.max(coupon.getMinDiscountPrice(),
                Math.min(coupon.getMaxDiscountPrice(), (int) (
                    context.getRemainPrice() * coupon.getAmount())));
        } else if (coupon.getMethod() == CouponMethod.STATIC) {
            couponPrice = Math.max(0, (int) (context.getRemainPrice() - coupon.getAmount()));
        }

        CouponPayment couponPayment = CouponPayment.builder().coupon(coupon).order(order).status(
            PaymentStatus.SUCCESS).game(game).build();
        if(accountsCoupons.getAmount() == 1) {
            accountsCouponsRepository.deleteById(accountsCoupons.getId());   // 1개면 삭제
        } else {
            accountsCoupons.updateAmount(accountsCoupons.getAmount() - 1);  // 아니면 1개 차감
            couponPaymentRepository.save(couponPayment);
        }
        context.setRemainPrice(context.getRemainPrice() - couponPrice);
        return Optional.of(couponPayment);
    }

    @Override
    void refund(PaymentRefundParameter parameter) { // 부분 취소 불가능
        CouponPayment payment = couponPaymentRepository.findById(parameter.getPaymentId()).get();
        Coupon coupon = payment.getCoupon();
        Order order = payment.getOrder();
        Account account = order.getAccount();
        AccountsCoupons accountsCoupons = accountsCouponsRepository.findFirstByAccountAndCoupon(account, coupon).orElse(null);
        if(accountsCoupons == null) {
            accountsCoupons = AccountsCoupons.builder().coupon(coupon).account(account).amount(1).build();
            accountsCouponsRepository.save(accountsCoupons);
        } else {
            accountsCoupons.updateAmount(accountsCoupons.getAmount() + 1);
        }

        payment.updateStatus(PaymentStatus.REFUNDED);
        payment.updateRefundedPoint(payment.getPrice());
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.COUPON;
    }
}
