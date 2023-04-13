package com.teemo.shopping.coupon.repository;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.CouponIssuePoliciesAccounts;
import com.teemo.shopping.coupon.domain.CouponIssuePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssuePoliciesAccountsRepository extends JpaRepository<CouponIssuePoliciesAccounts, Long> {
    boolean existsCouponIssuePoliciesByAccount(Account account);
}
