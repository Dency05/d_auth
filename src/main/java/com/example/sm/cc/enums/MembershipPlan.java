package com.example.sm.cc.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public enum MembershipPlan {
    LIFETIME("lifetime"),
    TWELVE_YEAR("12_year"),
    SIX_YEAR("6 Year"),
    TWO_MONTH("2 months");

    private String value;
    MembershipPlan(String value){
        this.value= value;
    }

    public static Set<String> toSet(){
        return Arrays.stream(values()).map(MembershipPlan::getValue).collect(Collectors.toSet());
    }
}
