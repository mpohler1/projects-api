package com.masonpohler.api.security;

import com.masonpohler.api.environment.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AuthenticationController {
    private static final String ADMIN_USERNAME_VARIABLE_NAME = "ADMIN_USERNAME";
    private static final String ADMIN_PASSWORD_VARIABLE_NAME = "ADMIN_PASSWORD";

    @Autowired
    private Environment environment;

    @Autowired
    private JWTHandler tokenHandler;
    
    @PostMapping("/login")
    String login(@RequestBody Credentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        String adminUsername = environment.getEnv(ADMIN_USERNAME_VARIABLE_NAME);
        String adminPassword = environment.getEnv(ADMIN_PASSWORD_VARIABLE_NAME);

        if (username.equals(adminUsername) && password.equals(adminPassword)) {
            return tokenHandler.createToken(username, Authorities.ADMIN.toString());
        } else {
            throw new AccessDeniedException("Username or Password was incorrect.");
        }
    }
}
