package com.teemo.shopping.account;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.teemo.shopping.account.domain.Account;
import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.util.DelegatingServletInputStream;
import com.teemo.shopping.Main;
import com.teemo.shopping.account.exception.AccountAlreadyExist;
import com.teemo.shopping.account.service.AccountAuthenticationService;
import com.teemo.shopping.security.filter.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Main.class)
class AccountTest {

    @Autowired
    private AccountAuthenticationService accountAuthenticationService;
    @Autowired
    private LoginFilter filter;
    @Autowired
    private AccountRepository accountRepository;
    @Test
    @DisplayName("Login in test")
    void loginTest() throws Exception {

        //Given
        String username = "chickenWorld1231";
        String password = "123j-12dkspfjgb-hjgr";
        String adminUsername = "adminChicken";
        String adminPassword = "12319djvsdvsdokqKDSKSd120";
        String rawJSON = "{username:\"" + username + "\", password:\"" + password + "\"}";

        accountAuthenticationService.register(username, password);
        Long adminUserAccountId = accountAuthenticationService.register(adminUsername, adminPassword);
        Account adminAccount = accountRepository.findById(adminUserAccountId).get();
        adminAccount.updateIsAdmin(true);   // 어드민 유저로 업데이트


        HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
        when(mockedHttpServletRequest.getMethod()).thenReturn("POST");
        when(mockedHttpServletRequest.getInputStream()).thenReturn(
            new DelegatingServletInputStream(rawJSON));
        HttpServletResponse mockedHttpServletResponse = mock(HttpServletResponse.class);

        //When
        boolean isAuthenticated = false;
        try {
            isAuthenticated = filter.attemptAuthentication(mockedHttpServletRequest,
                mockedHttpServletResponse).isAuthenticated();
        } catch (Exception err) {
            System.err.println(err.getMessage());
        }

        //then
        assertTrue(isAuthenticated);
    }

    @Test
    @DisplayName("Register Test")
    void register() throws Exception {

        //Given
        String username = "dassaodj";
        String password = "g2g23fd3";

        //when
        boolean isSuccessRegister = true;
        boolean isOverrapedRegister = false;
        try {
            accountAuthenticationService.register(username, password);
        } catch (AccountAlreadyExist e) {
            isSuccessRegister = false;
        }

        try {
            accountAuthenticationService.register(username, password);
            isOverrapedRegister = true;
        } catch (AccountAlreadyExist e) {

        }

        //then
        assertTrue(isSuccessRegister);
        assertFalse(isOverrapedRegister);
    }
}
