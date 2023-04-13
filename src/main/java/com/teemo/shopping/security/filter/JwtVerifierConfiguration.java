package com.teemo.shopping.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

public class JwtVerifierConfiguration {
    @Bean
    @Qualifier("jwt")
    @Autowired
    JWTVerifier jwtVerifier (@Qualifier("jwt") Algorithm jwtAlgorithm) {
        return JWT.require(jwtAlgorithm)    //Bean 으로 등록
            .withIssuer("teemo_shopping")
            .build();
    }
}
