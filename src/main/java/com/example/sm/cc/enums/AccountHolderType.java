package com.example.sm.cc.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum AccountHolderType {
    BUSINESS("Business"),
    PERSONAL("Personal");

    private String value;
    AccountHolderType(String value){
        this.value= value;
    }
}
