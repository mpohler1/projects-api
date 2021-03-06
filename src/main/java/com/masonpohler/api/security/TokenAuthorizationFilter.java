package com.masonpohler.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class TokenAuthorizationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
            if (token != null) {
                AuthenticatedUser authenticatedUser = tokenService.validateToken(token);
                if (authenticatedUser.getAuthority() != null) {
                    setUpSpringAuthentication(authenticatedUser);
                } else {
                    SecurityContextHolder.clearContext();
                }
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);

        } catch (TokenValidationException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    private void setUpSpringAuthentication(AuthenticatedUser authenticatedUser) {
        String authority = authenticatedUser.getAuthority();

        List<SimpleGrantedAuthority> grantedAuthorities = new LinkedList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(authority));

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                authenticatedUser.getUsername(),
                null,
                grantedAuthorities
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
