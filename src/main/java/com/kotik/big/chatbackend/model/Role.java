package com.kotik.big.chatbackend.model;

import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public enum Role {
    USER(Set.of(Permission.BASIC));
    private final Set<Permission> permissions;

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
