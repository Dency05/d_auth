package com.example.sm.employee.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum WorkDay {
   WORKING_DAY("Working Day"),
   NON_WORKING_DAY("Non Working Day");

    private String value;
    WorkDay(String value){
        this.value= value;
    }
}
