package com.teemo.shopping.security.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    protected LoginFilter(@Autowired DaoAuthenticationProvider daoAuthenticationProvider) {
        super("/login", new ProviderManager(List.of(
            daoAuthenticationProvider
        )));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response)
        throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals("POST")) {  // only request on post method be passed
            throw new AuthenticationServiceException("POST method만 지원함");   //return null 대신에 throw 하는 이유
            // throw 해야  unSuccessfulAuthentication 실행되고 log 기록됨
        }

        ServletInputStream inputStream = request.getInputStream();
        String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        System.out.println(body);
        Map<String, Object> bodyMaps = new GsonJsonParser().parseMap(body);

        String username = ((String) bodyMaps.get("username"));
        String password = ((String) bodyMaps.get("password"));
        if (username == null) { // sufficient body
            throw new UsernameNotFoundException("유저이름 없음");
        }
        username = username.trim();
        password = (password == null) ? "" : password.trim();
        try {
            return this.getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {

            throw new BadCredentialsException("인증 실패");
        }
    }
}