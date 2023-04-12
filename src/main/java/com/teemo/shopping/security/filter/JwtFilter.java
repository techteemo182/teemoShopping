package com.teemo.shopping.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
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
    private Algorithm jwtAlgorithm;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        final String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
        }

        JWTVerifier verifier = JWT.require(jwtAlgorithm)    //Bean 으로 등록
            .withIssuer("teemo_shopping")
            .build();
        DecodedJWT decodedJWT = verifier.verify(token);
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

    }
}
