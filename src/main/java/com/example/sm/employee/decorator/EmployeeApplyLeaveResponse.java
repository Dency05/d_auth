package com.example.sm.employee.decorator;

import com.example.sm.employee.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeApplyLeaveResponse {
    String name;
    LeaveType leaveType;
    String fromDate;
    String toDate;
    String amount;
}
