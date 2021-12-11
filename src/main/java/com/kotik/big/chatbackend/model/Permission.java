package com.kotik.big.chatbackend.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;


@RequiredArgsConstructor
public enum Permission implements GrantedAuthority {
    BASIC("basic");
    private final String permission;

    @Override
    public String getAuthority() {
        return permission;
    }
}
