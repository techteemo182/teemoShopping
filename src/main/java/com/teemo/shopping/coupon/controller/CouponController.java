package com.teemo.shopping.coupon.controller;

import com.teemo.shopping.coupon.domain.CouponIssuePolicy;
import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.coupon.service.CouponService;
import com.teemo.shopping.security.PermissionChecker;
import com.teemo.shopping.security.PermissionUtil;
import com.teemo.shopping.security.enums.Role;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping(path = "/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;
    @Autowired
    private PermissionChecker permissionChecker;
    @Autowired
    private PermissionUtil permissionUtil;

    @GetMapping(path = "/{couponId}")
    public CouponDTO get(@PathVariable("couponId") Long couponId) throws Exception {
        return couponService.get(couponId);
    }
    @PostMapping(path = "/")
    public Long add(CouponDTO couponDTO) throws Exception {
        if(!permissionChecker.checkAdmin()) {
            throw new SecurityException("접근 권한 없음");
        }
        return couponService.add(couponDTO);
    }
    @DeleteMapping(path = "/{couponId}")
    public void remove(@PathVariable("couponId") Long couponId) throws Exception {
        if(!permissionChecker.checkAdmin()) {
            throw new SecurityException("접근 권한 없음");
        }
        couponService.remove(couponId);
    }
    @GetMapping(path = "/")
    public List<CouponDTO> list() {
        return couponService.list();
    }

}
