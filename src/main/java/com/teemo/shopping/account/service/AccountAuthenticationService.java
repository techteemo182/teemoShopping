package com.teemo.shopping.account.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.core.exception.ServiceException;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class AccountAuthenticationService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    @Qualifier("login")
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("jwt")
    private Algorithm jwtAlgorithm;

    @Transactional(readOnly = true)
    public String login(String username, String rawPassword) {
        Account account;
        String token;
        try {
            account = accountRepository.findByUsername(username).get();
        } catch (NoSuchElementException e) {
            throw ServiceException.of("존재하지 않는 아이디.");
        }
        if (!passwordEncoder.matches(rawPassword, account.getPassword())) {
            throw ServiceException.of("비밀번호 불일치.");
        }
        String role = "";
        if(account.isAdmin()) {
            role = "ROLE_ADMIN";
        }
        try {
            token = JWT.create()
                .withIssuer("teemo_shopping")
                .withExpiresAt(new Date().toInstant().plusSeconds(36000))    // 10시간
                .withPayload(Map.of("account_id", account.getId(), "role", role))
                .sign(jwtAlgorithm);
        } catch (Exception e) {
            throw ServiceException.of("JWT 토큰 생성 실패");
        }
        return token;
    }

    @Transactional(readOnly = true)
    public boolean checkUsername(String username) {
        return !accountRepository.existsByUsername(username);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long register(String username, String password) {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw ServiceException.of("이미 존재하는 아이디");
        }
        Account account = Account.builder().username(username)
            .password(passwordEncoder.encode(password))
            .build();
        account = accountRepository.save(account);
        return account.getId();
    }

    @Transactional
    public void updateAdmin(Long accountId, boolean isAdmin) {
        Account account = accountRepository.findById(accountId).get();
        account.updateIsAdmin(isAdmin);
    }
}
