package com.example.demo.account;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.util.DelegatingServletInputStream;
import com.teemo.shopping.Main;
import com.teemo.shopping.account.AccountAlreadyExist;
import com.teemo.shopping.account.AccountService;
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
    private AccountService accountService;
    @Autowired
    private LoginFilter filter;

    @Test
    @DisplayName("Login in test")
    void loginTest() throws Exception {

        //Given
        String username = "chickenWorld1231";
        String password = "123j-12dkspfjgb-hjgr";
        String rawJSON = "{username:\"" + username + "\", password:\"" + password + "\"}";
        try {
            accountService.register(username, password);    //<- 이것도 mocking 해야하나
        } catch (AccountAlreadyExist e) {

        }
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
            accountService.register(username, password);
        } catch (AccountAlreadyExist e) {
            isSuccessRegister = false;
        }

        try {
            accountService.register(username, password);
            isOverrapedRegister = true;
        } catch (AccountAlreadyExist e) {

        }

        //then
        assertTrue(isSuccessRegister);
        assertFalse(isOverrapedRegister);
    }
}
