package com.example.sm.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Locale;
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class RequestSession {

    JWTUser jwtUser;
    String timezone;
    Locale locale = Locale.ENGLISH;
}
