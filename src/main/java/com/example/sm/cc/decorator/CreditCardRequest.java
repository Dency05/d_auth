package com.example.sm.cc.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardRequest {

   String exp_Year;
   String exp_Month;
   String cvv;
   String cardNo;
   String zipcode;


}
