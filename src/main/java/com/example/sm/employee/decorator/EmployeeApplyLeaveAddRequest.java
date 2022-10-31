package com.example.sm.employee.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeApplyLeaveAddRequest {
    String name;
    String fromDate;
    String toDate;
    String amount;

}
