package com.masonpohler.api.security;

import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Data
class AuthenticatedUser {
    String username;
    List<SimpleGrantedAuthority> grantedAuthorities;
}
