package com.example.sm.employee.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum MaritalStatus {
   MARRIED("Married"),
   SINGLE("Single"),
   DIVORCE("Divorce"),
   SINGLE_PARENT("Single Parent"),
   WINDOW("Window");

   private String value;
    MaritalStatus(String value){
        this.value= value;
    }

}
