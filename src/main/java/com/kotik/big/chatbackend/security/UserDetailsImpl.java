package com.kotik.big.chatbackend.security;

import com.kotik.big.chatbackend.model.Permission;
import com.kotik.big.chatbackend.model.User;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;


@Data
public class UserDetailsImpl implements UserDetails {

    private final String username;
    private final String password;
    private final List<Permission> authorities;


    public UserDetailsImpl(User user) {
        username = user.getLogin();
        password = user.getPasswordSha();
        authorities = user.getRole().getPermissions();
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
