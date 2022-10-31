package com.example.sm.employee.decorator;

import com.example.sm.employee.enums.Designation;
import com.example.sm.employee.enums.MaritalStatus;
import com.example.sm.employee.enums.ResidenceStatus;
import com.example.sm.employee.enums.WorkDay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeResponse {
    String employeeName;
    Designation designation;
    Date birthDate;
    String mobileNo;
    String email;
    ResidenceStatus residenceStatus;
    MaritalStatus maritalStatus;
    WorkDay workDay;
    Double workHours;
    String Location;
    String Note;
}
