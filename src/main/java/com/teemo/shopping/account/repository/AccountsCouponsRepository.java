package com.teemo.shopping.account.repository;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.domain.AccountsCoupons;
import com.teemo.shopping.coupon.domain.Coupon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsCouponsRepository extends JpaRepository<AccountsCoupons, Long> {
    Optional<AccountsCoupons> findFirstByAccountAndCoupon(Account account, Coupon coupon);

}
