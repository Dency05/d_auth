package com.example.sm.auth.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum UserStatus {

    ACTIVE("Active"),
    INVITED("Invited");

    private String value;

    UserStatus(String value) {
        this.value = value;
    }
}

