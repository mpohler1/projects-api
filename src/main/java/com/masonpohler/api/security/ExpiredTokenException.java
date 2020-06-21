package com.masonpohler.api.security;

class ExpiredTokenException extends TokenValidationException {

    ExpiredTokenException() {
        super("Token has expired. Please login again to receive a new token.");
    }
}
