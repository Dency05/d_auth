package com.example.sm.employee.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum GivenFrom {
   FINANCIAL("Financial"),
   PAY_ROLL("Pay Roll"),
    COOPERATED("CoOperated");

   private String value;
    GivenFrom(String value){
        this.value= value;
    }
}
