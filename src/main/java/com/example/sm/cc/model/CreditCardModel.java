package com.example.sm.cc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection= "cc_creditCardDetail")
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardModel {
   @Id
   String id;
   String exp_Year;
   String exp_Month;
   String cvv;
   String cardNo;
   String zipcode;
   boolean softDelete= false;
}
