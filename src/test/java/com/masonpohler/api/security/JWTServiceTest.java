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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JWTServiceTest {
    private static final String API_SECRET_ENVIRONMENT_VARIABLE_NAME = "API_SECRET";
    private static final String API_SECRET = "secret";
    private static final String ID = "adminJWT";
    private static final String ADMIN_USERNAME = "root";
    private static final String ADMIN_AUTHORITY = Authorities.ADMIN.toString();
    private static final String AUTHORITY_CLAIM = "authority";
    private static final long EXPIRATION_TIME_IN_MILLISECONDS = 1000000L;
    private static final SignatureAlgorithm CORRECT_SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private static final SignatureAlgorithm INCORRECT_SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    @Mock
    private EnvironmentService mockedEnvironmentService;

    @InjectMocks
    private JWTService jwtService;

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
                () -> jwtService.createToken(
                        "",
                        "",
                        new Date(System.currentTimeMillis()),
                        new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS)
                )
        );
    }

    @Test
    void create_token_does_not_throw_exception_when_environment_service_does_not_throw_exception() {
        assertDoesNotThrow(() -> jwtService.createToken(
                "",
                "",
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS)
        ));
    }

    @Test
    void create_token_returns_expected_token() {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS);

        String expectedToken = createValidToken(issuedAt, expiration);
        String actualToken = jwtService.createToken(ADMIN_USERNAME, ADMIN_AUTHORITY, issuedAt, expiration);

        assertEquals(expectedToken, actualToken);
    }

    @Test
    void validate_token_throws_expired_token_exception_when_given_expired_token() {
        String token = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() - EXPIRATION_TIME_IN_MILLISECONDS))
                .compact();

        assertThrows(ExpiredTokenException.class, () -> jwtService.validateToken(token));
    }

    @Test
    void validate_token_throws_unsupported_token_exception_when_given_unsupported_token() {
        String token = Jwts.builder()
                .setPayload("Token Payload")
                .signWith(INCORRECT_SIGNATURE_ALGORITHM, API_SECRET.getBytes())
                .compact();

        assertThrows(UnsupportedTokenException.class, () -> jwtService.validateToken(token));
    }

    @Test
    void validate_token_throws_token_signature_exception_when_given_token_with_bad_signature() {
        String token = Jwts.builder()
                .setPayload("Token Payload")
                .signWith(CORRECT_SIGNATURE_ALGORITHM, (API_SECRET + "badSig").getBytes())
                .compact();

        assertThrows(TokenSignatureException.class, () -> jwtService.validateToken(token));
    }

    @Test
    void validate_token_throws_malformed_token_exception_when_given_malformed_token() {
        String malformedToken = "malformedToken";
        assertThrows(MalformedTokenException.class, () -> jwtService.validateToken(malformedToken));
    }

    @Test
    void validate_token_does_not_throw_exception_when_validating_valid_token() {
        String token = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS))
                .signWith(CORRECT_SIGNATURE_ALGORITHM, API_SECRET.getBytes())
                .compact();

        assertDoesNotThrow(() -> jwtService.validateToken(token));
    }

    @Test
    void validate_token_does_not_throw_exception_when_validating_token_created_by_create_token() {
        String token = jwtService.createToken(
                ADMIN_USERNAME,
                ADMIN_AUTHORITY,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS)
        );
        assertDoesNotThrow(() -> jwtService.validateToken(token));
    }

    @Test
    void validate_token_returns_expected_authenticated_user_when_given_valid_token() {
        String validToken = createValidToken(
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS)
        );

        AuthenticatedUser expectedAuthenticatedUser = new AuthenticatedUser();
        expectedAuthenticatedUser.setUsername(ADMIN_USERNAME);
        expectedAuthenticatedUser.setAuthority(ADMIN_AUTHORITY);

        AuthenticatedUser actualAuthenticatedUser = jwtService.validateToken(validToken);

        assertEquals(expectedAuthenticatedUser, actualAuthenticatedUser);
    }

    @Test
    void validate_token_returns_expected_authenticated_user_when_given_valid_token_created_by_create_token() {
        String validToken = jwtService.createToken(
                ADMIN_USERNAME,
                ADMIN_AUTHORITY,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISECONDS)
        );

        AuthenticatedUser expectedAuthenticatedUser = new AuthenticatedUser();
        expectedAuthenticatedUser.setUsername(ADMIN_USERNAME);
        expectedAuthenticatedUser.setAuthority(ADMIN_AUTHORITY);

        AuthenticatedUser actualAuthenticatedUser = jwtService.validateToken(validToken);

        assertEquals(expectedAuthenticatedUser, actualAuthenticatedUser);
    }

    private String createValidToken(Date issuedAt, Date expiration) {
        return Jwts.builder()
                .setId(ID)
                .setSubject(ADMIN_USERNAME)
                .claim(AUTHORITY_CLAIM, ADMIN_AUTHORITY)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(CORRECT_SIGNATURE_ALGORITHM, API_SECRET.getBytes())
                .compact();
    }
}
