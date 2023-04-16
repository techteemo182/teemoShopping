package com.teemo.shopping.security;

import com.teemo.shopping.security.enums.Role;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class PermissionChecker {

    @Autowired
    private PermissionUtil permissionUtil;

    public boolean checkAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();     //todo: Aspect로 만들어 보기
        if (!authentication.isAuthenticated()) {
            return false;
        }
        return true;
    }

    public boolean checkAdmin() {
        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();     //todo: Aspect로 만들어 보기
        boolean isAuthenticated = authentication.isAuthenticated();
        if (!isAuthenticated) {
            return false;
        }
        boolean isAdmin = authentication.getAuthorities().contains(Role.ADMIN.getAuthority());
        if (!isAdmin) {
            return false;
        }
        return true;
    }

    public boolean checkResourceOwner(Supplier<Long> accountIdSupplier) {
        if (!checkAuthenticated()) {
            return false;
        }
        Optional<Long> optionalAccountId = permissionUtil.getAuthenticatedAccountId();
        if (optionalAccountId.isEmpty() || !optionalAccountId.get()
            .equals(accountIdSupplier.get())) {
            return false;
        }
        return true;
    }

    public boolean checkResourceOwner(Long accountId) {
        return checkResourceOwner(() -> accountId);
    }
    public void checkAdminAndThrow() throws SecurityException{
        if(!checkAdmin()) {
            throw new SecurityException("접근 권한 없음.");
        }
    }
    public void checkAuthenticatedAndThrow() {
        if(!checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음.");
        }
    }
    public Long getAccountIdElseThrow() throws SecurityException{
        checkAuthenticatedAndThrow();
        try {
            return permissionUtil.getAuthenticatedAccountId().get();
        } catch (NoSuchElementException e) {
            throw new SecurityException("인증되어있지만 AccountId 없음");
        }
    }
}
