package com.teemo.shopping.account;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.teemo.shopping.account.repository.AccountRepository;
import com.teemo.shopping.security.filter.JwtFilter;
import com.teemo.shopping.util.DelegatingServletInputStream;
import com.teemo.shopping.Main;
import com.teemo.shopping.account.service.AccountAuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(classes = Main.class)
class SecurityTest {

    @Autowired
    private AccountAuthenticationService accountAuthenticationService;
    @Autowired
    private AccountRepository accountRepository;

    /**
     * 로그인 테스트
     *
     */
    @Autowired
    private JwtFilter jwtFilter;
    @Test
    void loginTest() throws Exception {
        //Given
        String username = "chickenWorld1231";
        String password = "123j-12dkspfjgb-hjgr";

        Long accountId = accountAuthenticationService.register(username, password);
        String token = accountAuthenticationService.login(username, password);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        //when
        jwtFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        //then
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        assertTrue(authentication.isAuthenticated());    // 로그인 검증
        assertNotEquals(authentication.getPrincipal(), null);
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
