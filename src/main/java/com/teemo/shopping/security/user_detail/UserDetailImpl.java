package com.teemo.shopping.security.user_detail;

import com.teemo.shopping.security.enums.Role;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor(staticName = "of")
public class UserDetailImpl implements UserDetails {

    private String username;
    private String password;
    private boolean isAdmin;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(isAdmin) {
            return List.of(Role.ADMIN.getAuthority());
        } else {
            return null;
        }
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
