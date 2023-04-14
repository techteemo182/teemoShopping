package com.teemo.shopping.account;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.teemo.shopping.account.service.AccountService;
import com.teemo.shopping.coupon.dto.CouponDTO;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.security.PermissionChecker;
import com.teemo.shopping.security.PermissionUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import jdk.jfr.ContentType;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private PermissionUtil permissionUtil;
    @Autowired
    private PermissionChecker permissionChecker;
    @Autowired
    private AccountService accountService;

    @PostMapping(path = "/follow/{gameId}")
    public String followGame(@RequestParam Long gameId) {
        if(!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        Long accountId = permissionUtil.getAuthenticatedAccountId().get();
        accountService.followGame(accountId, gameId);
        return "success";
    }
    @PostMapping(path = "/unfollow/{gameId}")
    public String unfollowGame(@RequestParam Long gameId) {
        if(!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        Long accountId = permissionUtil.getAuthenticatedAccountId().get();
        accountService.unfollowGame(accountId, gameId);
        return "success";
    }
    @PostMapping(path = "/add_coupon/{couponId}")
    public String addCoupon(@RequestParam Long couponId) {
        if(!permissionChecker.checkAdmin()) {
            throw new SecurityException("접근 권한 없음");
        }
        Long accountId = permissionUtil.getAuthenticatedAccountId().get();
        accountService.addCoupon(accountId, couponId, 1);
        return "success";
    }
    @PostMapping(path = "/remove_coupon/{couponId}")
    public String removeCoupon(@RequestParam Long couponId) {
        if(!permissionChecker.checkAdmin()) {
            throw new SecurityException("접근 권한 없음");
        }
        Long accountId = permissionUtil.getAuthenticatedAccountId().get();
        accountService.removeCoupon(accountId, couponId, 1);
        return "success";
    }
}