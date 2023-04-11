package com.teemo.shopping.security.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter


public enum Role {
    ADMIN("ROLE_ADMIN");
    Role(String name) {
        this.authority = new SimpleGrantedAuthority(name);
    }
    private SimpleGrantedAuthority authority;
}
