package com.teemo.shopping.order.service;

import com.teemo.shopping.order.domain.CouponPayment;
import com.teemo.shopping.order.domain.Order;
import com.teemo.shopping.order.enums.PaymentStatus;
import com.teemo.shopping.order.dto.PaymentRefundParameter;
import com.teemo.shopping.order.dto.payment_create_param.CouponPaymentCreateParam;
import com.teemo.shopping.order.repository.OrderRepository;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsCoupons;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsCouponsRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.repository.CouponRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.repository.GameRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouponPaymentService extends GameProductPaymentService<CouponPaymentCreateParam> {

    @Autowired
    private PaymentRepository<CouponPayment> couponPaymentRepository;

    @Autowired
    private AccountsCouponsRepository accountsCouponsRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Optional<Long> create(CouponPaymentCreateParam param) throws IllegalStateException {
        Game game = gameRepository.findById(param.getGameId()).get();

        if(param.getCouponId() == null) {
            return Optional.empty();
        }
        Coupon coupon = couponRepository.findById(param.getCouponId()).get();
        Account account = accountRepository.findById(param.getAccountId()).get();
        Order order = orderRepository.findById(param.getOrderId()).get();

        if (coupon.getMinFulfillPrice() > param.getAmount()) { //쿠폰 최소 금액 충족 여부
            throw new IllegalStateException("쿠폰의 최저 충족 금액을 충족하지 못함");
        }

        AccountsCoupons accountsCoupons = accountsCouponsRepository.findByAccountAndCoupon(account, coupon)
            .orElseThrow(() -> new IllegalStateException("계정이 쿠폰을 소유하고 있지 아니함"));
        int couponAmount = 0;
        int originalAmount = param.getAmount();
        if (coupon.getMethod().equals(CouponMethod.PERCENT)) {
            double couponDecimalPercent = coupon.getAmount() / 100d;
            couponAmount = (int) Math.max(coupon.getMinDiscountPrice(),
                Math.min(coupon.getMaxDiscountPrice(), originalAmount * couponDecimalPercent));
        } else if (coupon.getMethod().equals(CouponMethod.STATIC)) {
            couponAmount = (int) Math.min(originalAmount, coupon.getAmount());
        }
        CouponPayment couponPayment = CouponPayment.builder().coupon(coupon).order(order).status(
            PaymentStatus.SUCCESS).game(game).amount(couponAmount).build();

        if(accountsCoupons.getAmount() == 1) {
            accountsCouponsRepository.deleteById(accountsCoupons.getId());  // 쿠폰이 하나면 계정과 쿠폰 관계 삭제
        } else {
            accountsCoupons.updateAmount(accountsCoupons.getAmount() - 1);  // 아니면 1개 차감
        }
        couponPaymentRepository.save(couponPayment);
        return Optional.of(accountsCoupons.getId());
    }

    @Override
    void refund(PaymentRefundParameter parameter) { // 부분 취소 불가능
        CouponPayment payment = couponPaymentRepository.findById(parameter.getPaymentId()).get();
        Coupon coupon = payment.getCoupon();
        Order order = payment.getOrder();
        Account account = order.getAccount();
        AccountsCoupons accountsCoupons = accountsCouponsRepository.findByAccountAndCoupon(account, coupon).orElse(null);
        if(accountsCoupons == null) {
            accountsCoupons = AccountsCoupons.builder().coupon(coupon).account(account).amount(1).build();
            accountsCouponsRepository.save(accountsCoupons);
        } else {
            accountsCoupons.updateAmount(accountsCoupons.getAmount() + 1);
        }

        payment.updateStatus(PaymentStatus.REFUNDED);
        payment.updateRefundedPoint(payment.getAmount());
    }

    @Override
    public Class<CouponPayment> getTargetPaymentClass() {
        return CouponPayment.class;
    }
}
