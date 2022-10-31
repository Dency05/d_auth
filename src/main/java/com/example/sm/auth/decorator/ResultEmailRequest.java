package com.example.sm.auth.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResultEmailRequest {
    String fullName;
    String semester;
    String spi;
    String cgpi;
    String email;
}
