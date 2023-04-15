package com.teemo.shopping.coupon.controller;

import com.teemo.shopping.coupon.dto.CouponIssuePolicyDTO;
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
    @Autowired
    private PermissionUtil permissionUtil;

    @GetMapping(path = "/{couponIssuerId}")
    public CouponIssuePolicyDTO get(@PathVariable("couponIssuerId") Long couponIssuerId) throws Exception {
        return couponIssuerService.get(couponIssuerId);
    }
    @PostMapping(path = "")
    public Long add(CouponIssuePolicyDTO couponIssuePolicyDTO) throws Exception {
        if(!permissionChecker.checkAdmin()) {
            throw new SecurityException("접근 권한 없음");
        }
        return couponIssuerService.add(couponIssuePolicyDTO);
    }
    @DeleteMapping(path = "/{couponIssuerId}")
    public void remove(@PathVariable("couponIssuerId") Long couponIssuerId) throws Exception {
        if(!permissionChecker.checkAdmin()) {
            throw new SecurityException("접근 권한 없음");
        }
        couponIssuerService.remove(couponIssuerId);
    }
    @GetMapping(path = "/")
    public List<CouponIssuePolicyDTO> list() {
        return couponIssuerService.list();
    }

    @PostMapping(path = "/{couponIssuerId}/issue/accounts")
    public String Issue(@PathVariable("couponId") Long couponIssuerId) {
        if(!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        Long accountId = permissionUtil.getAuthenticatedAccountId().get();
        couponIssuerService.issueCoupon(accountId, couponIssuerId);
        return "success";
    }


}
