package com.example.sm.employee.repository;

import com.example.sm.employee.model.Employee;
import com.example.sm.employee.repository.EmployeeCustomRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends MongoRepository<Employee,String>, EmployeeCustomRepository {
    boolean existsByEmailAndSoftDeleteFalse(String email);

    List<Employee> findAllBySoftDeleteFalse();

   Optional<Employee> findByEmployeeNameAndSoftDeleteIsFalse(String employeeName);
}
