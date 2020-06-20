package com.masonpohler.api.security;

public interface TokenService {
    String createToken(String username, String authority);
    AuthenticatedUser validateToken(String token);
}
