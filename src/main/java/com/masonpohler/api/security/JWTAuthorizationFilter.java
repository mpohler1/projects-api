package com.masonpohler.api.security;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PREFIX = "token ";
    private static final String API_SECRET = System.getenv("API_SECRET");

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (requestContainsToken(httpServletRequest, httpServletResponse)) {
                Claims claims = validateToken(httpServletRequest);
                if (claims.get("authorities") != null) {
                    setUpSpringAuthentication(claims);
                } else {
                    SecurityContextHolder.clearContext();
                }
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    private boolean requestContainsToken(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        return authorizationHeader != null && authorizationHeader.startsWith(PREFIX);
    }

    private Claims validateToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER).replace(PREFIX, "");
        return Jwts.parser().setSigningKey(API_SECRET.getBytes()).parseClaimsJws(token).getBody();
    }

    private void setUpSpringAuthentication(Claims claims) {
        List<SimpleGrantedAuthority> grantedAuthorities = (List<SimpleGrantedAuthority>) claims.get("authorities");

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null,
                grantedAuthorities
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
