package com.masonpohler.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Date;
import java.util.List;

class JWTHandler {

    private static final String API_SECRET = System.getenv("API_SECRET");
    private static final Long EXPIRATION_TIME_IN_MILLISECONDS = 7200000L;

    static String createToken(String username, String authority) {
        List grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authority);

        return Jwts.builder()
                .setId("adminJWT")
                .setSubject(username)
                .claim("authorities", grantedAuthorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS))
                .signWith(SignatureAlgorithm.ES256, API_SECRET.getBytes())
                .compact();
    }

    static Claims validateToken(String token) {
        return Jwts.parser().setSigningKey(API_SECRET.getBytes()).parseClaimsJws(token).getBody();
    }
}
