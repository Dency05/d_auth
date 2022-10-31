package com.example.sm.auth.controller;

import com.example.sm.employee.service.EmployeeService;
import com.example.sm.common.decorator.*;
import com.example.sm.common.enums.EmployeeSortBy;
import com.example.sm.employee.decorator.*;
import com.example.sm.employee.enums.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    GeneralHelper generalHelper;

    @Autowired
    EmployeeService employeeService;

    @SneakyThrows
    @RequestMapping(name = "addEmployee",value = "/add",method = RequestMethod.POST)
    public DataResponse<EmployeeResponse> addEmployee(@RequestBody EmployeeRequest employeeRequest, @RequestParam Designation designation,
                                                      @RequestParam MaritalStatus maritalStatus, @RequestParam ResidenceStatus residenceStatus, @RequestParam WorkDay workDay){
        DataResponse<EmployeeResponse> dataResponse=new DataResponse<>();
        dataResponse.setData(employeeService.addEmployee(employeeRequest,designation,maritalStatus,residenceStatus,workDay));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "getAllEmployee",value = "/getAll",method = RequestMethod.GET)
    public ListResponse<EmployeeResponse> getAllEmployee() {
        ListResponse<EmployeeResponse> listResponse= new ListResponse<>();
        listResponse.setData(employeeService.getAllEmployee());
        listResponse.setStatus(Response.getSuccessResponse());
        return listResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "addEmployeeLeave",value = "/add/leave",method = RequestMethod.POST)
    public DataResponse<EmployeeApplyLeaveResponse> addEmployee(@RequestBody EmployeeApplyLeaveAddRequest employeeApplyLeaveAddRequest, @RequestParam LeaveType leaveType){
        DataResponse<EmployeeApplyLeaveResponse> dataResponse=new DataResponse<>();
        dataResponse.setData(employeeService.addEmployeeLeave(employeeApplyLeaveAddRequest,leaveType));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }


    @SneakyThrows
    @RequestMapping(name = "getAllEmployeeLeave",value = "/getAll/employeeLeave",method = RequestMethod.GET)
    public ListResponse<EmployeeApplyLeaveResponse> getAllEmployeeLeave() {
        ListResponse<EmployeeApplyLeaveResponse> listResponse= new ListResponse<>();
        listResponse.setData(employeeService.getAllEmployeeLeave());
        listResponse.setStatus(Response.getSuccessResponse());
        return listResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "getEmployeeByPagination",value = "/getAll/filter",method = RequestMethod.POST)
    public ListResponse<EmployeeResponse> getEmployeeByPagination(@RequestBody FilterSortRequest<EmployeeFilter, EmployeeSortBy> filterSortRequest){
        ListResponse<EmployeeResponse> listResponse = new ListResponse<>();
        EmployeeFilter filter = filterSortRequest.getFilter();
        FilterSortRequest.SortRequest<EmployeeSortBy> sort = filterSortRequest.getSort();
        Pagination pagination= filterSortRequest.getPage();
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getLimit());
        List<EmployeeResponse> employeeResponses= employeeService.getEmployeeByPagination(filter,sort,pageRequest);
        listResponse.setData(employeeResponses);
        listResponse.setStatus(Response.getOhkResponse());
        return listResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "addEmployeeDebt",value = "/add/employee/debt",method = RequestMethod.POST)
    public DataResponse<EmplDebtResponse> addEmployeeDebt(@RequestBody EmplAddDebtRequest emplAddDebtRequest , @RequestParam GivenFrom givenFrom,@RequestParam(value = "file") MultipartFile file){
        DataResponse<EmplDebtResponse> dataResponse=new DataResponse<>();
        dataResponse.setData(employeeService.addEmployeeDept(emplAddDebtRequest,givenFrom,file));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

}
