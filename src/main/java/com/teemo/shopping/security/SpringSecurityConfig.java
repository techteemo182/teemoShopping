package com.teemo.shopping.security;

import com.teemo.shopping.security.filter.JwtFilter;
import com.teemo.shopping.security.filter.OverseeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private OverseeFilter overseeFilter;
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf()
            .disable()
            .authorizeHttpRequests()
            .anyRequest()
            .permitAll()
            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(overseeFilter, ChannelProcessingFilter.class)
            .headers()
            .frameOptions()
            .disable()
            .and()
            .build();
    }


}

