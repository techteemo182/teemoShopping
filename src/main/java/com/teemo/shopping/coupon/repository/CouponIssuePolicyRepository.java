package com.teemo.shopping.coupon.repository;

import com.teemo.shopping.coupon.domain.Coupon;
import com.teemo.shopping.coupon.domain.CouponIssuePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssuePolicyRepository extends JpaRepository<CouponIssuePolicy, Long> {

}
