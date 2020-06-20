package com.masonpohler.api.security;

import com.masonpohler.api.environment.EnvironmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {
    private static final String ADMIN_USERNAME_ENVIRONMENT_VARIABLE_NAME = "ADMIN_USERNAME";
    private static final String ADMIN_PASSWORD_ENVIRONMENT_VARIABLE_NAME = "ADMIN_PASSWORD";
    private static final String ADMIN_USERNAME = "root";
    private static final String ADMIN_PASSWORD = "root";

    @Mock
    EnvironmentService mockedEnvironmentService;

    @Mock
    TokenService mockedTokenService;

    @InjectMocks
    AuthenticationController controller;

    @BeforeEach
    void set_up() {
        MockitoAnnotations.initMocks(this);
        when(mockedEnvironmentService.getEnv(ADMIN_USERNAME_ENVIRONMENT_VARIABLE_NAME)).thenReturn(ADMIN_USERNAME);
        when(mockedEnvironmentService.getEnv(ADMIN_PASSWORD_ENVIRONMENT_VARIABLE_NAME)).thenReturn(ADMIN_PASSWORD);
    }

    @Test
    void login_throws_missing_environment_variable_exception_when_admin_username_is_not_set() {
        when(mockedEnvironmentService.getEnv(ADMIN_USERNAME_ENVIRONMENT_VARIABLE_NAME)).thenThrow(MissingEnvironmentVariableException.class);
        Credentials goodCredentials = makeGoodCredentials();
        assertThrows(MissingEnvironmentVariableException.class, () -> controller.login(goodCredentials));
    }

    @Test
    void login_throws_missing_environment_variable_exception_when_admin_password_is_not_set() {
        when(mockedEnvironmentService.getEnv(ADMIN_PASSWORD_ENVIRONMENT_VARIABLE_NAME)).thenThrow(MissingEnvironmentVariableException.class);
        Credentials goodCredentials = makeGoodCredentials();
        assertThrows(MissingEnvironmentVariableException.class, () -> controller.login(goodCredentials));
    }

    @Test
    void login_throws_access_denied_exception_when_username_and_password_incorrect() {
        Credentials badCredentials = makeBadCredentials();
        assertThrows(AccessDeniedException.class, () -> controller.login(badCredentials));
    }

    @Test
    void login_does_not_throw_exception_when_username_and_password_are_correct_and_environment_variables_are_set() {
        Credentials goodCredentials = makeGoodCredentials();
        assertDoesNotThrow(() -> controller.login(goodCredentials));
    }

    @Test
    void login_returns_a_token_created_by_token_handler_when_given_good_credentials() {
        String expectedToken = "xxxxx.yyyyy.zzzzz";

        when(mockedTokenService.createToken(
                any(String.class),
                any(String.class),
                any(Date.class),
                any(Date.class))
        )
                .thenReturn(expectedToken);

        Credentials goodCredentials = makeGoodCredentials();
        String actualToken = controller.login(goodCredentials);

        assertEquals(expectedToken, actualToken);
    }

    Credentials makeBadCredentials() {
        Credentials badCredentials = new Credentials();
        badCredentials.setUsername(ADMIN_USERNAME + "badUsername");
        badCredentials.setPassword(ADMIN_PASSWORD + "badPassword");
        return badCredentials;
    }

    Credentials makeGoodCredentials() {
        Credentials goodCredentials = new Credentials();
        goodCredentials.setUsername(ADMIN_USERNAME);
        goodCredentials.setPassword(ADMIN_PASSWORD);
        return goodCredentials;
    }
}
