package com.teemo.shopping.security.Authentication;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthentication implements Authentication {
    private Long accountId;
    private List<GrantedAuthority> authorities = new ArrayList<>();
    public JwtAuthentication(Long accountId) {
        this.accountId = accountId;
    }
    public JwtAuthentication(Long accountId, GrantedAuthority authority) {
        this.accountId = accountId;
        authorities.add(authority);
    }
    public JwtAuthentication(Long accountId, List<GrantedAuthority> authorities) {
        this.accountId = accountId;
        this.authorities.addAll(authorities);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.subList(0, authorities.size() - 1);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return accountId;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return null;
    }
}
