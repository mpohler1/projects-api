package com.masonpohler.api.security;

import java.util.Date;

public interface TokenService {
    String createToken(String username, String authority, Date issuedAt, Date expiration);
    AuthenticatedUser validateToken(String token);
}
