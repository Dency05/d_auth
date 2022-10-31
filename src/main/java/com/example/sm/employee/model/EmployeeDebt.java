package com.example.sm.employee.model;

import com.example.sm.employee.enums.GivenFrom;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection= "employee_debt")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDebt {
    String  amount;
    String date;
    int installment;
    String guarantor;
    String approvedBy;
    GivenFrom givenFrom;
    Date createdDate;
    @JsonIgnore
    boolean softDelete= false;

}
