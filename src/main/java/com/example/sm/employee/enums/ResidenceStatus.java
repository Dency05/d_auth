package com.example.sm.employee.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum ResidenceStatus {
    VISA("visa"),
    RESIDENT("resident"),
    NOT_AVILABLE("n/a");

    private String value;
    ResidenceStatus(String value){
        this.value= value;
    }

}
