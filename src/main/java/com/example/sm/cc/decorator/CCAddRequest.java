package com.example.sm.cc.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCAddRequest {

   String membershipName;
   String details;
   Set<String> benefits;
   List<MembershipPlans> membershipPlan;
}

