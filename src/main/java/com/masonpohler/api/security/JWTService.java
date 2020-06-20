package com.masonpohler.api.security;

import com.masonpohler.api.environment.EnvironmentService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
class JWTService implements TokenService {
    private static final Long EXPIRATION_TIME_IN_MILLISECONDS = 7200000L;
    private static final String API_SECRET_ENVIRONMENT_VARIABLE_NAME = "API_SECRET";
    private static final String AUTHORITIES_CLAIM = "authorities";

    @Autowired
    private EnvironmentService environmentService;

    public String createToken(String username, String authority) {
        String apiSecret = environmentService.getEnv(API_SECRET_ENVIRONMENT_VARIABLE_NAME);

        List grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authority);

        return Jwts.builder()
                .setId("adminJWT")
                .setSubject(username)
                .claim(AUTHORITIES_CLAIM, grantedAuthorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS))
                .signWith(SignatureAlgorithm.ES256, apiSecret)
                .compact();
    }

    public AuthenticatedUser validateToken(String token) {
        String apiSecret = environmentService.getEnv(API_SECRET_ENVIRONMENT_VARIABLE_NAME);
        Claims claims = Jwts.parser().setSigningKey(apiSecret).parseClaimsJws(token).getBody();

        String username = claims.getSubject();
        List<SimpleGrantedAuthority> grantedAuthorities = (List<SimpleGrantedAuthority>) claims.get(AUTHORITIES_CLAIM);

        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setUsername(username);
        authenticatedUser.setGrantedAuthorities(grantedAuthorities);
        return authenticatedUser;
    }
}
