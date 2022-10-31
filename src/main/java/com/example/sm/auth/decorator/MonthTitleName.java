package com.example.sm.auth.decorator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthTitleName {

    List<UserDetailByMonth> userDetailByMonths;
    Set<String> title= new LinkedHashSet<>();
    double totalCount;
}
