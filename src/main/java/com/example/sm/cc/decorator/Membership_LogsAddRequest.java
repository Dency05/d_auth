package com.example.sm.cc.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Membership_LogsAddRequest {

    String membershipPlanId;
    String membershipPlanName;
    String membershipName;
    double amount;
}
