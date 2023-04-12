package com.teemo.shopping.account;

import com.teemo.shopping.account.dto.AccountDTO;
import com.teemo.shopping.account.service.AccountAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/account")
public class AccountAuthenticationController {
    @Autowired
    private AccountAuthenticationService accountAuthenticationService;
    @PostMapping(path = "/login")
    public String login(@RequestBody AccountDTO accountDTO) {
        return accountAuthenticationService.login(accountDTO.getUsername(), accountDTO.getPassword());
    }
}
