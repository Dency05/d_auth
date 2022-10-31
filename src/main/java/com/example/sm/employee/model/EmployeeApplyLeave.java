package com.example.sm.employee.model;

import com.example.sm.employee.enums.LeaveType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection= "employee_leave")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeApplyLeave {
    String name;
    LeaveType leaveType;
    String fromDate;
    String toDate;
    String amount;
    @JsonIgnore
    Date date;
    @JsonIgnore
    boolean softDelete = false;
    float day;
}
