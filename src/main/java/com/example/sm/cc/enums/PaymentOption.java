package com.example.sm.cc.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum PaymentOption {
    CREDIT_CARD("Credit Card"),
    E_CHECK("E_Check"),
    PAYPAL("PayPal");

    private String value;
    PaymentOption(String value){
        this.value= value;
    }
}
