package com.teemo.shopping.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class OverseeFilter extends OncePerRequestFilter {
    static int count = 0;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        System.out.println("요청[" + count++ + "] : {"
            + "\nMethod: " + request.getMethod()
            + "\nURL: " + request.getRequestURL()
            + "\n}");
        filterChain.doFilter(request, response);
    }
}
