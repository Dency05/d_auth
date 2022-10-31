package com.example.sm.common.decorator;

import com.example.sm.common.model.JWTUser;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Locale;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Component
public class RequestSession {
    JWTUser jwtUser;
    String timezone;
    Locale locale = Locale.ENGLISH;
}
