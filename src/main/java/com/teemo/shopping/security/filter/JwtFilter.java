package com.teemo.shopping.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.teemo.shopping.security.Authentication.JwtAuthentication;
import com.teemo.shopping.security.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("jwt")
    private JWTVerifier verifier;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        final String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || token.isEmpty()) { // Header 가 비어있으면 넘어가기
            filterChain.doFilter(request, response);
            return;
        }
        DecodedJWT decodedJWT;
        try {
            decodedJWT = verifier.verify(token);
        } catch (TokenExpiredException e) {
            response.setStatus(401);
            response.setContentType("text/plain");
            response.getOutputStream().write("JWT 토큰 만료".getBytes(
                StandardCharsets.UTF_8));
            return;
        }
        GsonJsonParser gsonJsonParser = new GsonJsonParser();   //Bean으로 등록
        Map<String, Object> payload = gsonJsonParser.parseMap(
                new String(Base64.decodeBase64(decodedJWT.getPayload()), StandardCharsets.UTF_8));

        Long accountId =  ((Double)payload.get("account_id")).longValue();
        String role = (String) payload.get("role");
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if(role.equals("ROLE_ADMIN")) {
            grantedAuthorities.add(Role.ADMIN.getAuthority());
        }
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new JwtAuthentication(accountId, grantedAuthorities));
        SecurityContextHolder.setContext(context);
        filterChain.doFilter(request, response);
    }
}
