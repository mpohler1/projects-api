package com.masonpohler.api.security;

import com.masonpohler.api.environment.EnvironmentService;
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
    private EnvironmentService environmentService;

    @Autowired
    private TokenService tokenService;
    
    @PostMapping("/login")
    String login(@RequestBody Credentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        String adminUsername = environmentService.getEnv(ADMIN_USERNAME_VARIABLE_NAME);
        String adminPassword = environmentService.getEnv(ADMIN_PASSWORD_VARIABLE_NAME);

        if (username.equals(adminUsername) && password.equals(adminPassword)) {
            return tokenService.createToken(username, Authorities.ADMIN.toString());
        } else {
            throw new AccessDeniedException("Username or Password was incorrect.");
        }
    }
}