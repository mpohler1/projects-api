package com.masonpohler.api.security;

class UnsupportedTokenException extends TokenValidationException {

    UnsupportedTokenException() {
        super("The token you provided is not supported. Please login to receive a valid token.");
    }
}
