package com.example.sm.cc.model;

import com.example.sm.cc.enums.AccountHolderType;
import com.example.sm.cc.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection= "E_checkDetails")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class E_CheckModel {
    String firstName;
    String lastName;
    String bankName;
    String accountingNumber;
    String routingNumber;
    AccountHolderType accountHolderType;
    AccountType accountType;

}
