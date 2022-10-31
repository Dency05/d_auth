package com.example.sm.employee.decorator;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeRequest {
    String employeeName;
    @JsonFormat(pattern="yyyy-MM-dd")
    Date birthDate;
    String mobileNo;
    String email;
    Double workHours;
    String Location;
    String Note;
}
