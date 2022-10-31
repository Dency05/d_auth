package com.example.sm.cc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "membership_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Membership_Logs {

    @Id
    String id;
    String membershipPlanId;
    String userId;
    String membershipPlanName;
    String membershipName;
    double amount;
    Date date;
    @JsonIgnore
    boolean softDelete= false;

}
