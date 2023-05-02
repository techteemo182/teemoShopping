package com.teemo.shopping.coupon.controller;

import com.teemo.shopping.coupon.dto.CouponIssuerDTO;
import com.teemo.shopping.coupon.service.CouponIssuerService;
import com.teemo.shopping.security.PermissionChecker;
import com.teemo.shopping.security.PermissionUtil;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/coupon-issuers")
public class CouponIssuerController {
    @Autowired
    private CouponIssuerService couponIssuerService;
    @Autowired
    private PermissionChecker permissionChecker;


    @Operation(operationId = "쿠폰발급자 조회", summary = "쿠폰발급자 조회", tags = {"쿠폰발급자"})
    @GetMapping(path = "/{couponIssuerId}")
    public CouponIssuerDTO get(@PathVariable("couponIssuerId") Long couponIssuerId) throws Exception {
        return couponIssuerService.get(couponIssuerId);
    }
    @Operation(operationId = "쿠폰발급자 생성", summary = "쿠폰발급자 생성", tags = {"쿠폰발급자"})
    @PostMapping(path = "")
    public Long add(CouponIssuerDTO couponIssuerDTO) throws Exception {
        permissionChecker.checkAdminAndThrow();
        return couponIssuerService.add(couponIssuerDTO);
    }
    @Operation(operationId = "쿠폰발급자 삭제", summary = "쿠폰발급자 삭제", tags = {"쿠폰발급자"})
    @DeleteMapping(path = "/{couponIssuerId}")
    public void remove(@PathVariable("couponIssuerId") Long couponIssuerId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        couponIssuerService.remove(couponIssuerId);
    }
    @Operation(operationId = "쿠폰발급자 리스트", summary = "쿠폰발급자 리스트", tags = {"쿠폰발급자"})
    @GetMapping(path = "")
    public List<CouponIssuerDTO> list() {
        return couponIssuerService.list();
    }

    @Operation(operationId = "계정에 쿠폰 발급", summary = "계정에 쿠폰 발급", tags = {"쿠폰발급자"})
    @PostMapping(path = "/{couponIssuerId}/issue/accounts")
    public String Issue(@PathVariable("couponId") Long couponIssuerId) {
        Long accountId = permissionChecker.getAccountIdElseThrow();
        couponIssuerService.issueCoupon(accountId, couponIssuerId);
        return "success";
    }


}
