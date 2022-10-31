package com.example.sm.cc.decorator;

import com.example.sm.cc.enums.MembershipPlan;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlans {

    MembershipPlan membershipPlan;
    double amount;
    int number;
    @ApiModelProperty(hidden = true)
    Date date;
    @JsonIgnore
    boolean active= false;

}
