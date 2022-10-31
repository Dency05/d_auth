package com.example.sm.auth.decorator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserResultResponse {
    String id;
    List<Result> results;
    String fullName;
    double totalMark;
    double average;
    double count;
}
