package com.example.sm.auth.decorator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResultByStatus {
    String id;
   List<Result> result;
   List<Result> results;
    String fullName;

}
