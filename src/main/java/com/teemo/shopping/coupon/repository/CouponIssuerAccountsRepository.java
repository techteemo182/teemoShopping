package com.teemo.shopping.coupon.repository;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.coupon.domain.CouponIssuerAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssuerAccountsRepository extends JpaRepository<CouponIssuerAccounts, Long> {
    boolean existsCouponIssuePoliciesByAccount(Account account);
}
