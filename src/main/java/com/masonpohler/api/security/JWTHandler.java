package com.masonpohler.api.security;

import com.masonpohler.api.environment.Environment;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
class JWTHandler {
    private static final Long EXPIRATION_TIME_IN_MILLISECONDS = 7200000L;
    private static final String API_SECRET_ENVIRONMENT_VARIABLE_NAME = "API_SECRET";

    @Autowired
    private Environment environment;

    String createToken(String username, String authority) {
        String apiSecret = environment.getEnv(API_SECRET_ENVIRONMENT_VARIABLE_NAME);

        List grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authority);

        return Jwts.builder()
                .setId("adminJWT")
                .setSubject(username)
                .claim("authorities", grantedAuthorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS))
                .signWith(SignatureAlgorithm.ES256, apiSecret.getBytes())
                .compact();
    }

    Claims validateToken(String token) {
        String apiSecret = environment.getEnv(API_SECRET_ENVIRONMENT_VARIABLE_NAME);
        return Jwts.parser().setSigningKey(apiSecret.getBytes()).parseClaimsJws(token).getBody();
    }
}
