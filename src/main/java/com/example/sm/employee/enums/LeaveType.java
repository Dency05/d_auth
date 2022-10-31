package com.example.sm.employee.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum LeaveType {
    PAID_VACATION1("Paid Vacation1"),
    PAID_VACATION2("Paid Vacation2"),
    PAID_LEAVE1("Paid Leave1"),
    PAID_LEAVE2("Paid Leave2");

    private String value;
    LeaveType(String value){
        this.value= value;
    }
}
