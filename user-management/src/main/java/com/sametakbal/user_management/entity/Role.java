package com.sametakbal.user_management.entity;

import lombok.Getter;

@Getter
public enum Role {
    USER(1, "ROLE_USER"),
    ADMIN(2, "ROLE_ADMIN");

    private final Integer id;
    private final String name;

    Role(Integer id, String name){
        this.id = id;
        this.name = name;
    }
}
