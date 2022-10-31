package com.example.sm.employee.service;

import com.example.sm.common.decorator.EmployeeFilter;
import com.example.sm.common.decorator.FilterSortRequest;
import com.example.sm.common.enums.EmployeeSortBy;
import com.example.sm.employee.decorator.*;
import com.example.sm.employee.enums.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

public interface EmployeeService {
    EmployeeResponse addEmployee(EmployeeRequest employeeRequest, Designation designation, MaritalStatus maritalStatus, ResidenceStatus residenceStatus, WorkDay workDay) throws InvocationTargetException, IllegalAccessException;

    List<EmployeeResponse> getAllEmployee() throws InvocationTargetException, IllegalAccessException;

    EmployeeApplyLeaveResponse addEmployeeLeave(EmployeeApplyLeaveAddRequest employeeApplyLeaveAddRequest, LeaveType leaveType) throws InvocationTargetException, IllegalAccessException, ParseException;

    List<EmployeeApplyLeaveResponse> getAllEmployeeLeave() throws InvocationTargetException, IllegalAccessException;

    List<EmployeeResponse> getEmployeeByPagination(EmployeeFilter filter, FilterSortRequest.SortRequest<EmployeeSortBy> sort, PageRequest pageRequest) throws InvocationTargetException, IllegalAccessException;

    EmplDebtResponse addEmployeeDept(EmplAddDebtRequest emplAddDebtRequest, GivenFrom givenFrom, MultipartFile file);
}
