package com.teemo.shopping.order.service.payment;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsCoupons;
import com.teemo.shopping.account.repository.AccountsCouponsRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.enums.CouponMethod;
import com.teemo.shopping.coupon.repository.CouponsGameCategoriesRepository;
import com.teemo.shopping.coupon.repository.CouponsGamesRepository;
import com.teemo.shopping.game.domain.Game;
import com.teemo.shopping.game.domain.GameCategory;
import com.teemo.shopping.game.repository.GameCategoriesGamesRepository;
import com.teemo.shopping.order.domain.CouponPayment;
import com.teemo.shopping.order.domain.Payment;
import com.teemo.shopping.order.enums.PaymentStates;
import com.teemo.shopping.order.repository.PaymentRepository;
import com.teemo.shopping.order.service.context.OrderCreateContext;
import com.teemo.shopping.order.service.observer.PaymentStateUpdatePublisher;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponPaymentService extends PaymentService {

    @Autowired
    private PaymentRepository<CouponPayment> couponPaymentRepository;
    @Autowired
    private AccountsCouponsRepository accountsCouponsRepository;
    @Autowired
    private CouponsGamesRepository couponsGamesRepository;
    @Autowired
    private CouponsGameCategoriesRepository couponsGameCategoriesRepository;
    @Autowired
    private GameCategoriesGamesRepository gameCategoriesGamesRepository;
    @Autowired
    private PaymentStateUpdatePublisher paymentStateUpdatePublisher;


    @Override
    public Payment create(OrderCreateContext context) throws IllegalStateException {
        Game game = context.getGame().get();
        Optional<Coupon> couponOptional = context.getPreparedData().getGameCouponMap().get(game);
        Coupon coupon = couponOptional.orElseThrow(
            () -> new IllegalStateException("쿠폰이 지정되지 않았습니다."));
        Integer beforeAmount = context.getAmount();
        int amount = 0;
        if (coupon.getMinFulfillPrice() > beforeAmount) {
            throw new IllegalStateException("쿠폰의 최저 충족 금액을 충족하지 못함");
        }
        if (coupon.getMethod().equals(CouponMethod.PERCENT)) {
            double couponDecimalPercent = coupon.getAmount() / 100d;
            amount = (int) Math.max(coupon.getMinDiscountPrice(),
                Math.min(coupon.getMaxDiscountPrice(), beforeAmount * couponDecimalPercent));
        } else if (coupon.getMethod().equals(CouponMethod.STATIC)) {
            amount = (int) Math.min(beforeAmount, coupon.getAmount());
        }

        CouponPayment couponPayment = CouponPayment.builder().coupon(coupon).account(context.getPreparedData()
                .getAccount())
            .amount(amount).game(game).build();
        return couponPayment;
    }

    public void refund(Long paymentId, int amount) { // 부분 취소 불가능
        CouponPayment payment = couponPaymentRepository.findById(paymentId).get();
        if (payment.getState().equals(PaymentStates.SUCCESS)) {
            throw new IllegalStateException("환불 가능한 상태가 아님.");
        }
        Coupon coupon = payment.getCoupon();
        Account account = payment.getAccount();
        AccountsCoupons accountsCoupons = accountsCouponsRepository.findByAccountAndCoupon(account,
            coupon).orElse(null);
        if (accountsCoupons == null) {
            accountsCoupons = AccountsCoupons.builder().coupon(coupon).account(account).amount(1)
                .build();
            accountsCouponsRepository.save(accountsCoupons);
        } else {
            accountsCoupons.updateAmount(accountsCoupons.getAmount() + 1);
        }

        payment.setState(PaymentStates.REFUNDED);
        payment.setRefundedAmount(payment.getAmount());
        paymentStateUpdatePublisher.publish(payment.getPaymentStateUpdateSubscriberTypes(), payment.getId());
    }

    @Override
    @Transactional
    public void pay(Long paymentId) {
        CouponPayment payment = couponPaymentRepository.findById(paymentId).get();

        if (!payment.getState().equals(PaymentStates.PENDING)) {
            throw new IllegalStateException("결제 가능한 상태가 아님.");
        }

        Coupon coupon = payment.getCoupon();
        Account account = payment.getAccount();
        Game game = payment.getGame();
        try {
            List<GameCategory> gameCategories = gameCategoriesGamesRepository.findAllByGame(game)
                .stream().map(gameCategoriesGames -> gameCategoriesGames.getGameCategory())
                .toList();
            if (LocalDateTime.now().isAfter(
                coupon.getExpiredAt())) {        // Improve: now() 시점을 서버가 Request 받은 시점으로 하고, UTC-0 을 표준으로 하기
                throw new IllegalStateException("유효 기간이 지난 쿠폰입니다.");
            }

            List<Game> applicableGames = couponsGamesRepository.findAllByCoupon(coupon).stream()
                .map(couponsGames -> couponsGames.getGame()).toList();
            List<GameCategory> applicableGameCategories = couponsGameCategoriesRepository.findAllByCoupon(
                    coupon).stream()
                .map(couponsGameCategories -> couponsGameCategories.getGameCategory()).toList();
            boolean isApplicableGame = applicableGames.contains(game);
            boolean isApplicableGameCategory = applicableGameCategories.stream().anyMatch(
                (applicableGameCategory) -> gameCategories.contains(
                    applicableGameCategory)); // improve: hash or sorted comparison
            if (!(coupon.isCanApplyToAll() || isApplicableGame || isApplicableGameCategory)) {
                throw new IllegalStateException("쿠폰을 적용할 수 없는 게임입니다.");
            }
            AccountsCoupons accountsCoupons = accountsCouponsRepository.findByAccountAndCoupon(
                    account, coupon)
                .orElseThrow(() -> new IllegalStateException("계정이 쿠폰을 소유하고 있지 아니함"));

            if (accountsCoupons.getAmount() == 1) {
                accountsCouponsRepository.deleteById(
                    accountsCoupons.getId());  // 쿠폰이 하나면 계정과 쿠폰 관계 삭제
            } else {
                accountsCoupons.updateAmount(accountsCoupons.getAmount() - 1);  // 아니면 1개 차감
            }
            payment.setState(PaymentStates.SUCCESS);
        } catch (Exception e) {
            payment.setState(PaymentStates.CANCEL);
        }
        paymentStateUpdatePublisher.publish(payment.getPaymentStateUpdateSubscriberTypes(), payment.getId());
    }

    @Override
    public Class<? extends Payment> getPaymentClass() {
        return CouponPayment.class;
    }
}
