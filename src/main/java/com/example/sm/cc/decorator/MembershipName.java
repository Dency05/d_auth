package com.example.sm.cc.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MembershipName {

    List<MembershipPlanDetail> membershipPlanDetails;
    double totalCount;
    Set<String> titles= new LinkedHashSet<>();
}
