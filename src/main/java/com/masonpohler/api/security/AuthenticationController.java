package com.masonpohler.api.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    private static String ADMIN_USERNAME = System.getenv("ADMIN_USERNAME");
    private static String ADMIN_PASSWORD = System.getenv("ADMIN_PASSWORD");
    
    @PostMapping("/login")
    String login(@RequestBody Credentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            return JWTHandler.createToken(username, Authorities.ADMIN.toString());
        } else {
            throw new AccessDeniedException("Username or Password was incorrect.");
        }
    }
}
