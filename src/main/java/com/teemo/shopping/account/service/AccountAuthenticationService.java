package com.teemo.shopping.account.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.dto.AccountDTO;
import com.teemo.shopping.account.exception.AccountAlreadyExist;
import com.teemo.shopping.account.exception.AccountNotFound;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.security.enums.Role;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.json.GsonJsonParser;
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

    @Transactional
    public String login(String username, String rawPassword) {
        Account account;
        String token;
        try {
            account = accountRepository.findByUsername(username).get();
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("없는 아이디");
        }
        if (!passwordEncoder.matches(rawPassword, account.getPassword())) {
            throw new IllegalArgumentException("비밀번호 불일치");
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
            throw new UnknownError("JWT Token 생성 실패");
        }
        return token;
    }

    @Transactional
    public boolean validate(String username, String password) throws AccountNotFound {
        Optional<Account> accountOptional = accountRepository.findByUsername(username);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFound();
        }
        Account account = accountOptional.get();

        return passwordEncoder.matches(account.getPassword(), password);
    }

    public boolean checkUsername(String username) {
        return !accountRepository.findByUsername(username).isEmpty();
    }

    @Transactional
    public Long register(String username, String password) throws AccountAlreadyExist {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("이미 같은 아이디를 가진 계정이 존재함.");
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
