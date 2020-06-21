package com.masonpohler.api.security;

class MalformedTokenException extends TokenValidationException {

    MalformedTokenException() {
        super("Token is malformed. Please login to receive a new token.");
    }
}
