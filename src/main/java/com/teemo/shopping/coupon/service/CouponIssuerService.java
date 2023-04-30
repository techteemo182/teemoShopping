package com.teemo.shopping.coupon.service;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsCoupons;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsCouponsRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.CouponIssuerAccounts;
import com.teemo.shopping.coupon.domain.CouponIssuer;
import com.teemo.shopping.coupon.dto.CouponIssuerDTO;
import com.teemo.shopping.coupon.repository.CouponIssuerAccountsRepository;
import com.teemo.shopping.coupon.repository.CouponIssuerRepository;
import com.teemo.shopping.coupon.repository.CouponRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponIssuerService {

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponIssuerRepository couponIssuerRepository;

    @Autowired
    private CouponIssuerAccountsRepository couponIssuerAccountsRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountsCouponsRepository accountsCouponsRepository;
    @Autowired
    private CouponService couponService;
    @Transactional
    public Long add(CouponIssuerDTO couponIssuerDTO)
        throws Exception {
        Coupon coupon = couponRepository.findById(couponIssuerDTO.getCouponId()).get();
        CouponIssuer couponIssuer = CouponIssuer.builder().coupon(coupon)
            .amount(couponIssuerDTO.getAmount())
            .isNewAccount(couponIssuerDTO.isNewAccount())
            .isFirstCome(couponIssuerDTO.isFirstCome())
            .remainAmount(couponIssuerDTO.getRemainAmount())
            .startAt(couponIssuerDTO.getStartAt()).endAt(couponIssuerDTO.getEndAt())
            .build();
        couponIssuer = couponIssuerRepository.save(couponIssuer);
        return couponIssuer.getId();
    }

    @Transactional
    public void remove(Long couponIssuePolicyId) throws Exception {
        if(!couponIssuerRepository.existsById(couponIssuePolicyId)) {
            throw new IllegalStateException("존재 하지 않는 쿠폰 정책임.");
        }
        couponIssuerRepository.deleteById(couponIssuePolicyId);
    }

    @Transactional(readOnly = true)
    public CouponIssuerDTO get(Long couponIssuePolicyId) throws Exception {
        return CouponIssuerDTO.from(couponIssuerRepository.findById(couponIssuePolicyId).get());
    }

    @Transactional(readOnly = true)
    public List<CouponIssuerDTO> list() {
        return couponIssuerRepository.findAll().stream().map(couponIssuePolicy -> CouponIssuerDTO.from(couponIssuePolicy)).toList();
    }

    @Transactional
    public void issueCoupon(Long accountId, Long couponIssuerId) {
        CouponIssuer couponIssuer = couponIssuerRepository.findById(
            couponIssuerId).get();
        Account account = accountRepository.findById(accountId).get();

        if (couponIssuerAccountsRepository.existsCouponIssuePoliciesByAccount(account)) {
            throw new IllegalStateException("이미 쿠폰이 발급됨");
        }
        if (couponIssuer.getEndAt().isBefore(LocalDateTime.now())
            || couponIssuer.getStartAt().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("쿠폰 발급가능 기간이 아님");
        }

        if (couponIssuer.isNewAccount()) {
            boolean isNewAccount = account.getCreatedAt()
                .isAfter(LocalDateTime.now().minusDays(10));
            if (!isNewAccount) {
                throw new IllegalStateException("새로운 유저가 아님");
            }
        }
        int amount = couponIssuer.getAmount();
        if (couponIssuer.isFirstCome()) {
            if (couponIssuer.getRemainAmount() <= 0) {
                throw new IllegalStateException("쿠폰이 전부 소진되었습니다.");
            }
            amount = Math.min(couponIssuer.getRemainAmount(), couponIssuer.getAmount());
            couponIssuer.updateRemainAmount(couponIssuer.getRemainAmount() - amount);
        }

        Coupon coupon = couponIssuer.getCoupon();
        AccountsCoupons accountsCoupons = accountsCouponsRepository.findByAccountAndCoupon(account,
                coupon)
            .orElse(AccountsCoupons.builder().account(account).coupon(coupon).amount(0).build());
        accountsCoupons.updateAmount(accountsCoupons.getAmount() + amount);     // Account 에 쿠폰 주기
        accountsCouponsRepository.save(accountsCoupons);

        CouponIssuerAccounts couponIssuerAccounts = CouponIssuerAccounts.builder()
            .account(account).couponIssuer(couponIssuer).build();
        couponIssuerAccountsRepository.save(couponIssuerAccounts);    // 쿠폰 발급 기록 남기기

    }

}
