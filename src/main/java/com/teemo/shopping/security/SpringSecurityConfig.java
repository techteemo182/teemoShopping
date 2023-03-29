package com.teemo.shopping.security;

import com.teemo.shopping.security.filter.LoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    @Autowired
    private LoginFilter loginFilter;
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests()
            .anyRequest()
            .permitAll()
            .and()
            .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }


}

