package com.example.sm.common.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Component
public class TokenResponse<T> {
    T data;
    Response status;
    String token;
}
