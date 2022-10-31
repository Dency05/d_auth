package com.example.sm.employee.model;

import com.example.sm.employee.enums.Designation;
import com.example.sm.employee.enums.MaritalStatus;
import com.example.sm.employee.enums.ResidenceStatus;
import com.example.sm.employee.enums.WorkDay;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection= "employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    String employeeName;
    Designation designation;
    @JsonFormat(pattern="yyyy-MM-dd")
    Date birthDate;
    String mobileNo;
    String email;
    ResidenceStatus residenceStatus;
    MaritalStatus maritalStatus;
    WorkDay workDay;
    Double workHours;
    String Location;
    String Note;
    Date date;
    @JsonIgnore
    boolean softDelete= false;
}
