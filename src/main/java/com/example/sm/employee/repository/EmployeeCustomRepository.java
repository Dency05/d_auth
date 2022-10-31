package com.example.sm.employee.repository;

import com.example.sm.employee.decorator.EmployeeResponse;
import com.example.sm.common.decorator.EmployeeFilter;
import com.example.sm.common.decorator.FilterSortRequest;
import com.example.sm.common.enums.EmployeeSortBy;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface EmployeeCustomRepository {
    List<EmployeeResponse> findAllEmployeeByFilterAndSortAndPage(EmployeeFilter filter, FilterSortRequest.SortRequest
            <EmployeeSortBy> sort, PageRequest pagination) throws InvocationTargetException, IllegalAccessException;
}
