package com.example.sm.bookshop.enums;

import lombok.Getter;

@Getter
public enum Amount{
    TWENTY_FIVE_RUPEES(25),
    FIFTY_RUPEES(50),
    SEVENTY_FIVE_RUPEES(75),
    HUNDRED_RUPEES(100);
    final Integer value;
    Amount(int i) {
        this.value = i;
    }
}
