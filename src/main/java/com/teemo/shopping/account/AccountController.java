package com.teemo.shopping.account;

import com.teemo.shopping.account.service.AccountService;
import com.teemo.shopping.security.PermissionChecker;
import com.teemo.shopping.security.PermissionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
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
