package com.example.sm.employee.repository;

import com.example.sm.employee.model.EmployeeDebt;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeDebtRepository extends MongoRepository<EmployeeDebt,String> {
}
