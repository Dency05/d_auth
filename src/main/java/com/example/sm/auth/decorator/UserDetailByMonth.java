package com.example.sm.auth.decorator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailByMonth {

    String month;
    String dateOfMonth;
    String id;
    Set<String> userIds;
    double count;

}
