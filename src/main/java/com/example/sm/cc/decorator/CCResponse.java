package com.example.sm.cc.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCResponse {

    String membershipName;
    String details;
    List<MembershipPlans> membershipPlan;
    Set<String> benefits;
    double amount;
}
