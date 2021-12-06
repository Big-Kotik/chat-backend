package com.kotik.big.chatbackend.model;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
public enum Role {
    USER(List.of(Permission.BASIC));
    private final List<Permission> permissions;

    public List<Permission> getPermissions() {
        return permissions;
    }
}
