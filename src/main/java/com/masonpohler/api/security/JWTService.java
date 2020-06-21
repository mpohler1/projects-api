package com.masonpohler.api.security;

import com.masonpohler.api.environment.EnvironmentService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
class JWTService implements TokenService {
    private static final String API_SECRET_ENVIRONMENT_VARIABLE_NAME = "API_SECRET";
    private static final String AUTHORITIES_CLAIM = "authorities";

    @Autowired
    private EnvironmentService environmentService;

    public String createToken(String username, String authority, Date issuedAt, Date expiration) {
        String apiSecret = environmentService.getEnv(API_SECRET_ENVIRONMENT_VARIABLE_NAME);

        List grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authority);

        return Jwts.builder()
                .setId("adminJWT")
                .setSubject(username)
                .claim(AUTHORITIES_CLAIM, grantedAuthorities)
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
        List<SimpleGrantedAuthority> grantedAuthorities = (List<SimpleGrantedAuthority>) claims.get(AUTHORITIES_CLAIM);

        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setUsername(username);
        authenticatedUser.setGrantedAuthorities(grantedAuthorities);
        return authenticatedUser;
    }
}
