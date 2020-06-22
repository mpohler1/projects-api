package com.masonpohler.api.security;

import lombok.Data;

@Data
class AuthenticatedUser {
    private String username;
    private String authority;
}
