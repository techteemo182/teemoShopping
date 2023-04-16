package com.teemo.shopping.coupon.controller;

import com.teemo.shopping.coupon.dto.CouponIssuerDTO;
import com.teemo.shopping.coupon.service.CouponIssuerService;
import com.teemo.shopping.security.PermissionChecker;
import com.teemo.shopping.security.PermissionUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * TODO:
 *
 */
@RestController
@RequestMapping(path = "/coupon-issuer")
public class CouponIssuerController {
    @Autowired
    private CouponIssuerService couponIssuerService;
    @Autowired
    private PermissionChecker permissionChecker;

    @GetMapping(path = "/{couponIssuerId}")
    public CouponIssuerDTO get(@PathVariable("couponIssuerId") Long couponIssuerId) throws Exception {
        return couponIssuerService.get(couponIssuerId);
    }
    @PostMapping(path = "")
    public Long add(CouponIssuerDTO couponIssuerDTO) throws Exception {
        permissionChecker.checkAdminAndThrow();
        return couponIssuerService.add(couponIssuerDTO);
    }
    @DeleteMapping(path = "/{couponIssuerId}")
    public void remove(@PathVariable("couponIssuerId") Long couponIssuerId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        couponIssuerService.remove(couponIssuerId);
    }
    @GetMapping(path = "/")
    public List<CouponIssuerDTO> list() {
        return couponIssuerService.list();
    }

    @PostMapping(path = "/{couponIssuerId}/issue/accounts")
    public String Issue(@PathVariable("couponId") Long couponIssuerId) {
        Long accountId = permissionChecker.getAccountIdElseThrow();
        couponIssuerService.issueCoupon(accountId, couponIssuerId);
        return "success";
    }


}
