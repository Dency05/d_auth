package com.example.sm.cc.model;

import com.example.sm.cc.decorator.MembershipPlans;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Document(collection= "cc_membership")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCModel {
    @Id
    String id;

    String membershipName;

    List<MembershipPlans> membershipPlan;

    String details;

    Date date;

    Set<String> benefits;

    @JsonIgnore
    boolean softDelete= false;

    @JsonIgnore
    boolean discount= false;

    @JsonIgnore
    boolean active= false;

}
