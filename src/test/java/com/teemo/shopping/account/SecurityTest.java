package com.teemo.shopping.account;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.util.DelegatingServletInputStream;
import com.teemo.shopping.Main;
import com.teemo.shopping.account.service.AccountAuthenticationService;
import com.teemo.shopping.security.filter.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Main.class)
class SecurityTest {

    @Autowired
    private AccountAuthenticationService accountAuthenticationService;
    @Autowired
    private LoginFilter filter;
    @Autowired
    private AccountRepository accountRepository;

    /**
     * 로그인 테스트
     *
     */
    @Test
    void loginTest() throws Exception {
        //Given
        String username = "chickenWorld1231";
        String password = "123j-12dkspfjgb-hjgr";
        String rawJSON = "{username:\"" + username + "\", password:\"" + password + "\"}";

        accountAuthenticationService.register(username, password);

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
        assertTrue(isAuthenticated);    // 로그인 검증
    }


    /**
     * 회원가입 테스트
     * @throws Exception
     */
    @Test
    void register() {
        //Given
        String username = "dassaodj";
        String password = "g2g23fd3";

        //when
        boolean isSuccessRegister = true;
        try {
            accountAuthenticationService.register(username, password);
        } catch (Exception e) {
            isSuccessRegister = false;
        }

        //then
        assertTrue(isSuccessRegister);
    }

    /**
     * 중복 가입 테스트
     */
    @Test
    void doubleRegister() {

        //Given
        String username = "dassaodj";
        String password = "g2g23fd3";

        //when
        accountAuthenticationService.register(username, password);

        //then
        assertThrows(IllegalStateException.class, () -> {   //가입 중복 검증
            accountAuthenticationService.register(username, password);
        });
    }
}
