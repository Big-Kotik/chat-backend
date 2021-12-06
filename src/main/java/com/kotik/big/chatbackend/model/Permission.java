package com.kotik.big.chatbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;


@AllArgsConstructor
public enum Permission implements GrantedAuthority {
    BASIC("basic");
    private final String permission;

    @Override
    public String getAuthority() {
        return permission;
    }
}
