package com.example.sm.auth.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor

public enum UserSortBy {
    AGE("age"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email"),

    MIDDLE_NAME("middleName");

    @JsonIgnore
    private String value;
    UserSortBy(String value) {
        this.value = value;
    }

    Map<String,String> toMap() {
        Map<String,String> map=new HashMap<>();
        map.put("value",this.toString());
        return map;
}}
