package com.teemo.shopping.security;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.repository.AccountRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PermissionUtil {
    @Autowired
    private AccountRepository accountRepository;
    public Optional<Long> getAuthenticatedAccountId() {
        if(!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            return Optional.empty();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.of((Long)authentication.getPrincipal());
    }
}
