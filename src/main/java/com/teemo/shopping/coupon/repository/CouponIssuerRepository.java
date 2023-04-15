package com.teemo.shopping.coupon.repository;

import com.teemo.shopping.coupon.domain.CouponIssuer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssuerRepository extends JpaRepository<CouponIssuer, Long> {

}
