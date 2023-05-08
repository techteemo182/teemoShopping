package com.teemo.shopping.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthenticatoinConfiguration {

    @Bean
    @Qualifier("login")
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    @Qualifier("jwt")
    Algorithm jwtAlgorithm(@Value("${jwt.privkey}") String privateKeyPem,
        @Value("${jwt.pubkey}") String publicKeyPem)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        String stringPublicKey = publicKeyPem;
        String stringPrivateKey = privateKeyPem;
        stringPublicKey = stringPublicKey.replace("-----BEGIN PUBLIC KEY-----", "")
            .replaceAll(System.lineSeparator(), "")
            .replace("-----END PUBLIC KEY-----", "").trim();
        stringPrivateKey = stringPrivateKey.replace("-----BEGIN PRIVATE KEY-----", "")
            .replaceAll(System.lineSeparator(), "")
            .replace("-----END PRIVATE KEY----", "").trim();
        byte[] encodedPublicKey = Base64.decodeBase64(stringPublicKey);
        byte[] encodedPrivateKey = Base64.decodeBase64(stringPrivateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(
            privateKeySpec);
        Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
        return algorithm;
    }

    @Bean
    @Qualifier("jwt")
    JWTVerifier jwtVerifier (Algorithm jwtAlgorithm) {
        return JWT.require(jwtAlgorithm)    //Bean 으로 등록
            .withIssuer("teemo_shopping")
            .build();
    }
}