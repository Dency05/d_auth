package com.example.sm.common.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
@Getter
@NoArgsConstructor
public enum EmployeeSortBy {
    BIRTHDATE("birthDate"),
    EMPLOYEE_NAME("employeeName");

    @JsonIgnore
    private String value;

   EmployeeSortBy(String value) {
        this.value = value;
    }

    Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("value", this.toString());
        return map;
    }
}
