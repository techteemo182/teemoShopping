package com.teemo.shopping.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {
    SecurityFilterChain DefaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests()
            .anyRequest()
            .permitAll()
            .and()
            .build();
    }
}
