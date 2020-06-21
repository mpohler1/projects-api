package com.masonpohler.api.security;

class TokenSignatureException extends TokenValidationException {

    TokenSignatureException() {
        super("The token provided was not signed by this API. Please login to receive a valid token.");
    }
}
