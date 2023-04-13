package com.teemo.shopping.coupon.service;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsCoupons;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.account.repository.AccountsCouponsRepository;
import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.CouponIssuePoliciesAccounts;
import com.teemo.shopping.coupon.domain.CouponIssuePolicy;
import com.teemo.shopping.coupon.dto.CouponIssuePolicyDTO;
import com.teemo.shopping.coupon.repository.CouponIssuePoliciesAccountsRepository;
import com.teemo.shopping.coupon.repository.CouponIssuePolicyRepository;
import com.teemo.shopping.coupon.repository.CouponRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponIssuePolicyService {

    @Autowired
    CouponRepository couponRepository;
    @Autowired
    CouponIssuePolicyRepository couponIssuePolicyRepository;

    @Autowired
    CouponIssuePoliciesAccountsRepository couponIssuePoliciesAccountsRepository;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountsCouponsRepository accountsCouponsRepository;
    @Transactional
    public Long add(CouponIssuePolicyDTO couponIssuePolicyDTO)
        throws Exception { //todo: game, gameCategory 추가 하기
        Coupon coupon = couponRepository.findById(couponIssuePolicyDTO.getCouponId()).get();
        CouponIssuePolicy couponIssuePolicy = CouponIssuePolicy.builder().coupon(coupon)
            .amount(couponIssuePolicyDTO.getAmount())
            .isNewAccount(couponIssuePolicyDTO.isNewAccount())
            .isFirstCome(couponIssuePolicyDTO.isFirstCome())
            .remainAmount(couponIssuePolicyDTO.getRemainAmount())
            .startAt(couponIssuePolicyDTO.getStartAt()).endAt(couponIssuePolicyDTO.getEndAt())
            .build();
        couponIssuePolicy = couponIssuePolicyRepository.save(couponIssuePolicy);
        return couponIssuePolicy.getId();
    }

    @Transactional
    public void remove(Long couponIssuePolicyId) throws Exception {
        if(!couponIssuePolicyRepository.existsById(couponIssuePolicyId)) {
            throw new IllegalStateException("존재 하지 않는 쿠폰 정책임.");
        }
        couponIssuePolicyRepository.deleteById(couponIssuePolicyId);
    }

    @Transactional(readOnly = true)
    public CouponIssuePolicyDTO get(Long couponIssuePolicyId) throws Exception {
        return CouponIssuePolicyDTO.from(couponIssuePolicyRepository.findById(couponIssuePolicyId).get());
    }

    @Transactional(readOnly = true)
    public List<CouponIssuePolicyDTO> list() {
        return couponIssuePolicyRepository.findAll().stream().map(couponIssuePolicy -> CouponIssuePolicyDTO.from(couponIssuePolicy)).toList();
    }

    @Transactional
    public void issueCoupon(Long accountId, Long couponIssuePolicyId) {
        CouponIssuePolicy couponIssuePolicy = couponIssuePolicyRepository.findById(
            couponIssuePolicyId).get();
        Account account = accountRepository.findById(accountId).get();

        if (couponIssuePoliciesAccountsRepository.existsCouponIssuePoliciesByAccount(account)) {
            throw new IllegalStateException("이미 쿠폰이 발급됨");
        }
        if (couponIssuePolicy.getEndAt().isBefore(LocalDateTime.now())
            || couponIssuePolicy.getStartAt().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("쿠폰 발급가능 기간이 아님");
        }

        if (couponIssuePolicy.isNewAccount()) {
            boolean isNewAccount = account.getCreatedAt()
                .isAfter(LocalDateTime.now().minusDays(10));
            if (!isNewAccount) {
                throw new IllegalStateException("새로운 유저가 아님");
            }
        }
        int amount = couponIssuePolicy.getAmount();
        if (couponIssuePolicy.isFirstCome()) {
            if (couponIssuePolicy.getRemainAmount() <= 0) {
                throw new IllegalStateException("쿠폰이 전부 소진되었습니다.");
            }
            amount = Math.min(couponIssuePolicy.getRemainAmount(), couponIssuePolicy.getAmount());
            couponIssuePolicy.updateRemainAmount(amount);
        }

        Coupon coupon = couponIssuePolicy.getCoupon();
        AccountsCoupons accountsCoupons = accountsCouponsRepository.findByAccountAndCoupon(account,
                coupon)
            .orElse(AccountsCoupons.builder().account(account).coupon(coupon).amount(0).build());
        accountsCoupons.updateAmount(accountsCoupons.getAmount() + amount);     // Account 에 쿠폰 주기
        accountsCouponsRepository.save(accountsCoupons);

        CouponIssuePoliciesAccounts couponIssuePoliciesAccounts = CouponIssuePoliciesAccounts.builder()
            .account(account).couponIssuePolicy(couponIssuePolicy).build();
        couponIssuePoliciesAccountsRepository.save(couponIssuePoliciesAccounts);    // 쿠폰 발급 기록 남기기

    }
}
