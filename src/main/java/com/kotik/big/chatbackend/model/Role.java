package com.kotik.big.chatbackend.model;

import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public enum Role {
    USER(List.of(Permission.BASIC));
    private final List<Permission> permissions;

    public List<Permission> getPermissions() {
        return permissions;
    }
}
