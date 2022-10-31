package com.example.sm.employee.enums;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum Designation {

   MANAGER("Manager"),
   SUPERVISOR("Supervisor"),
   CASHIER("Cashier");

    private String value;
    Designation(String value){
        this.value= value;
    }
}
