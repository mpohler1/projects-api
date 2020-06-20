package com.masonpohler.api.security;

import com.masonpohler.api.environment.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {
    private static final String ADMIN_USERNAME = "root";
    private static final String ADMIN_PASSWORD = "root";

    @Mock
    Environment mockedEnvironment;

    @Mock
    JWTHandler mockedTokenHandler;

    @InjectMocks
    AuthenticationController controller;

    @BeforeEach
    void set_up() {
        MockitoAnnotations.initMocks(this);
        when(mockedEnvironment.getEnv("ADMIN_USERNAME")).thenReturn(ADMIN_USERNAME);
        when(mockedEnvironment.getEnv("ADMIN_PASSWORD")).thenReturn(ADMIN_PASSWORD);
    }

    @Test
    void create_token_throws_access_denied_exception_when_username_and_password_incorrect() {
        Credentials badCredentials = makeBadCredentials();
        assertThrows(AccessDeniedException.class, () -> controller.login(badCredentials));
    }

    @Test
    void create_token_does_not_throw_access_denied_exception_when_username_and_password_are_correct() {
        Credentials goodCredentials = makeGoodCredentials();
        assertDoesNotThrow(() -> controller.login(goodCredentials));
    }

    @Test
    void create_token_returns_a_token_created_by_token_handler_when_given_good_credentials() {
        String expectedToken = "xxxxx.yyyyy.zzzzz";

        when(mockedTokenHandler.createToken(any(String.class), any(String.class)))
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
