package com.teemo.shopping.coupon.controller;

import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.coupon.dto.CouponIssuePolicyDTO;
import com.teemo.shopping.coupon.service.CouponIssuePolicyService;
import com.teemo.shopping.coupon.service.CouponService;
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
@RequestMapping(path = "/coupon_issue_policies")
public class CouponIssuePolicyController {
    @Autowired
    private CouponIssuePolicyService couponIssuePolicyService;
    @Autowired
    private PermissionChecker permissionChecker;
    @Autowired
    private PermissionUtil permissionUtil;

    @GetMapping(path = "/{couponIssuePolicyId}")
    public CouponIssuePolicyDTO get(@PathVariable("couponIssuePolicyId") Long couponIssuePolicyId) throws Exception {
        return couponIssuePolicyService.get(couponIssuePolicyId);
    }
    @PostMapping(path = "/")
    public Long add(CouponIssuePolicyDTO couponIssuePolicyDTO) throws Exception {
        if(!permissionChecker.checkAdmin()) {
            throw new SecurityException("접근 권한 없음");
        }
        return couponIssuePolicyService.add(couponIssuePolicyDTO);
    }
    @DeleteMapping(path = "/{couponIssuePolicyId}")
    public void remove(@PathVariable("couponIssuePolicyId") Long couponIssuePolicyId) throws Exception {
        if(!permissionChecker.checkAdmin()) {
            throw new SecurityException("접근 권한 없음");
        }
        couponIssuePolicyService.remove(couponIssuePolicyId);
    }
    @GetMapping(path = "/")
    public List<CouponIssuePolicyDTO> list() {
        return couponIssuePolicyService.list();
    }

    @PostMapping(path = "/{couponIssuePolicyId}/issue")
    public String Issue(@PathVariable("couponId") Long couponIssuePolicyId) {
        if(!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        Long accountId = permissionUtil.getAuthenticatedAccountId().get();
        couponIssuePolicyService.issueCoupon(accountId, couponIssuePolicyId);
        return "success";
    }

}
