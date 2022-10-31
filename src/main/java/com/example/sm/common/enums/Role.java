package com.example.sm.common.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public enum Role  {

    STUDENT("Student"),
    DEPARTMENT("Department"),
    ADMIN("Admin"),
    ANONYMOUS("Anonymous"),
    SYSTEM("System");

    private String value;
    Role(String value){
        this.value= value;
    }


}
