package com.masonpohler.api.security;

import com.masonpohler.api.environment.EnvironmentService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class JWTServiceTest {
    private static final String API_SECRET_ENVIRONMENT_VARIABLE_NAME = "API_SECRET";
    private static final String API_SECRET = "secret";
    private static final String ADMIN_USERNAME = "root";
    private static final String ADMIN_AUTHORITY = Authorities.ADMIN.toString();
    private static final long EXPIRATION_TIME_IN_MILLISECONDS = 1000000L;
    private static final SignatureAlgorithm CORRECT_SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private static final SignatureAlgorithm INCORRECT_SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    @Mock
    private EnvironmentService mockedEnvironmentService;

    @InjectMocks
    private JWTService tokenService;

    @BeforeEach
    void set_up() {
        MockitoAnnotations.initMocks(this);
        when(mockedEnvironmentService.getEnv(API_SECRET_ENVIRONMENT_VARIABLE_NAME)).thenReturn(API_SECRET);
    }

    @Test
    void create_token_throws_missing_environment_variable_exception_when_api_secret_is_not_set() {
        when(mockedEnvironmentService.getEnv(API_SECRET_ENVIRONMENT_VARIABLE_NAME))
                .thenThrow(MissingEnvironmentVariableException.class);
        assertThrows(
                MissingEnvironmentVariableException.class,
                () -> tokenService.createToken(
                        "",
                        "",
                        new Date(System.currentTimeMillis()),
                        new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS)
                )
        );
    }

    @Test
    void create_token_does_not_throw_exception_when_environment_service_does_not_throw_exception() {
        assertDoesNotThrow(() -> tokenService.createToken(
                "",
                "",
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS)
        ));
    }

    @Test
    void validate_token_throws_expired_token_exception_when_given_expired_token() {
        String token = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() - EXPIRATION_TIME_IN_MILLISECONDS))
                .compact();

        assertThrows(ExpiredTokenException.class, () -> tokenService.validateToken(token));
    }

    @Test
    void validate_token_throws_unsupported_token_exception_when_given_unsupported_token() {
        String token = Jwts.builder()
                .setPayload("Token Payload")
                .signWith(INCORRECT_SIGNATURE_ALGORITHM, API_SECRET.getBytes())
                .compact();

        assertThrows(UnsupportedTokenException.class, () -> tokenService.validateToken(token));
    }

    @Test
    void validate_token_throws_token_signature_exception_when_given_token_with_bad_signature() {
        String token = Jwts.builder()
                .setPayload("Token Payload")
                .signWith(CORRECT_SIGNATURE_ALGORITHM, (API_SECRET + "badSig").getBytes())
                .compact();

        assertThrows(TokenSignatureException.class, () -> tokenService.validateToken(token));
    }

    @Test
    void validate_token_throws_malformed_token_exception_when_given_malformed_token() {
        String malformedToken = "malformedToken";
        assertThrows(MalformedTokenException.class, () -> tokenService.validateToken(malformedToken));
    }

    @Test
    void validate_token_does_not_throw_exception_when_validating_valid_token() {
        String token = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS))
                .signWith(CORRECT_SIGNATURE_ALGORITHM, API_SECRET.getBytes())
                .compact();

        assertDoesNotThrow(() -> tokenService.validateToken(token));
    }

    @Test
    void validate_token_does_not_throw_exception_when_validating_token_created_by_create_token() {
        String token = tokenService.createToken(
                ADMIN_USERNAME,
                ADMIN_AUTHORITY,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS)
        );
        assertDoesNotThrow(() -> tokenService.validateToken(token));
    }
}
