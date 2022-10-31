package com.example.sm.auth.decorator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder
public class UserMinMaxMarkSemResponse {
    String id;
    String fullName;
    Result minArray;
    Result maxArray;
    Double min;
    Double max;
}
