package com.example.sm.cc.decorator;

import com.example.sm.cc.enums.AccountHolderType;
import com.example.sm.cc.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class E_CheckRequest {
    String firstName;
    String lastName;
    String bankName;
    String accountingNumber;
    String routingNumber;
    AccountHolderType accountHolderType;
    AccountType accountType;

}
