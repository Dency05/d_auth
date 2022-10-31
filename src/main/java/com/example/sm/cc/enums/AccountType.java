package com.example.sm.cc.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum AccountType {
    CHECKING("Checking"),
    SAVINGS("Savings");

    private String value;
    AccountType(String value){
        this.value= value;
    }
}
