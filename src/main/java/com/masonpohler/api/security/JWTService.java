package com.masonpohler.api.security;

import com.masonpohler.api.environment.EnvironmentService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
class JWTService implements TokenService {
    private static final String API_SECRET_ENVIRONMENT_VARIABLE_NAME = "API_SECRET";
    private static final String AUTHORITY_CLAIM = "authority";

    @Autowired
    private EnvironmentService environmentService;

    public String createToken(String username, String authority, Date issuedAt, Date expiration) {
        String apiSecret = environmentService.getEnv(API_SECRET_ENVIRONMENT_VARIABLE_NAME);

        return Jwts.builder()
                .setId("adminJWT")
                .setSubject(username)
                .claim(AUTHORITY_CLAIM, authority)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, apiSecret.getBytes())
                .compact();
    }

    public AuthenticatedUser validateToken(String token) throws TokenValidationException {
        try {
            return doValidation(token);

        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();

        } catch (UnsupportedJwtException e) {
            throw new UnsupportedTokenException();

        } catch (MalformedJwtException e) {
            throw new MalformedTokenException();

        } catch (SignatureException e) {
            throw new TokenSignatureException();
        }
    }

    private AuthenticatedUser doValidation(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException {
        String apiSecret = environmentService.getEnv(API_SECRET_ENVIRONMENT_VARIABLE_NAME);
        Claims claims = Jwts.parser().setSigningKey(apiSecret.getBytes()).parseClaimsJws(token).getBody();

        String username = claims.getSubject();
        String authority = claims.get(AUTHORITY_CLAIM, String.class);

        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setUsername(username);
        authenticatedUser.setAuthority(authority);
        return authenticatedUser;
    }
}
