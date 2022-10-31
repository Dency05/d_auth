package com.example.sm.common.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum PasswordEncryptionType {

    MD5("MD5"),
    BCRYPT("BCrypt");
    String name;
    PasswordEncryptionType(String name) {
        this.name = name;
    }

    public static List<Map<String, String>> toList() {
        return Arrays.stream(values()).map(PasswordEncryptionType::toMap).collect(Collectors.toList());
    }

    Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("value", this.toString());
        System.out.println("map:"+map);
        return map;
    }
}