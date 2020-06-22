package com.masonpohler.api.security;

import org.springframework.core.NestedRuntimeException;

abstract class TokenValidationException extends NestedRuntimeException {

    TokenValidationException(String msg) {
        super(msg);
    }
}
