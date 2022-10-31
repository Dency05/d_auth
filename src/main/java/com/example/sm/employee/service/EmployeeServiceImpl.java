package com.example.sm.employee.service;


import com.example.sm.employee.model.Employee;
import com.example.sm.employee.model.EmployeeApplyLeave;
import com.example.sm.employee.model.EmployeeDebt;
import com.example.sm.employee.repository.EmployeeApplyLeaveRepository;
import com.example.sm.employee.repository.EmployeeDebtRepository;
import com.example.sm.employee.repository.EmployeeRepository;
import com.example.sm.common.constant.MessageConstant;
import com.example.sm.common.decorator.EmployeeFilter;
import com.example.sm.common.decorator.FilterSortRequest;
import com.example.sm.common.decorator.NullAwareBeanUtilsBean;
import com.example.sm.common.enums.EmployeeSortBy;
import com.example.sm.common.exception.AlreadyExistException;
import com.example.sm.common.exception.EmptyException;
import com.example.sm.common.exception.InvaildRequestException;
import com.example.sm.common.exception.NotFoundException;
import com.example.sm.common.model.AdminConfiguration;
import com.example.sm.common.service.AdminConfigurationService;
import com.example.sm.employee.decorator.*;
import com.example.sm.employee.enums.*;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeApplyLeaveRepository employeeApplyLeaveRepository;

    @Autowired
    EmployeeDebtRepository employeeDebtRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AdminConfigurationService adminService;

    @Autowired
    NullAwareBeanUtilsBean nullAwareBeanUtilsBean;

    @Override
    public EmployeeResponse addEmployee(EmployeeRequest employeeRequest, Designation designation, MaritalStatus maritalStatus, ResidenceStatus residenceStatus, WorkDay workDay) throws InvocationTargetException, IllegalAccessException {
        EmployeeResponse employeeResponse = new EmployeeResponse();
        checkEmployeeDetails(employeeRequest);
        if((designation!=null)&& (maritalStatus!=null)&&(residenceStatus!=null)&&(workDay!=null)) {
            Employee employee = modelMapper.map(employeeRequest, Employee.class);
            employee.setDesignation(designation);
            employee.setMaritalStatus(maritalStatus);
            employee.setResidenceStatus(residenceStatus);
            employee.setWorkDay(workDay);
            employee.setDate(new Date());
            employeeRepository.save(employee);
            modelMapper.map(employee, employeeResponse);
            return employeeResponse;
        }
      else{
          throw new InvaildRequestException(MessageConstant.NOT_EMPTY);
        }
    }

    @Override
    public List<EmployeeResponse> getAllEmployee() throws InvocationTargetException, IllegalAccessException {
        List<Employee> employee = employeeRepository.findAllBySoftDeleteFalse();
        List<EmployeeResponse> employeeResponse = new ArrayList<>();
        if (!CollectionUtils.isEmpty(employee)) {
            for (Employee employee1 : employee) {
                EmployeeResponse employeeResponse1 = new EmployeeResponse();
                nullAwareBeanUtilsBean.copyProperties(employeeResponse1, employee1);
                employeeResponse.add(employeeResponse1);
            }
        }
        return employeeResponse;
    }

    @Override
    public EmployeeApplyLeaveResponse addEmployeeLeave(EmployeeApplyLeaveAddRequest employeeApplyLeaveAddRequest, LeaveType leaveType) throws InvocationTargetException, IllegalAccessException, ParseException {
            checkApplyLeave(employeeApplyLeaveAddRequest,leaveType);
            EmployeeApplyLeave employeeApplyLeave = modelMapper.map(employeeApplyLeaveAddRequest, EmployeeApplyLeave.class);
            employeeApplyLeave.setLeaveType(leaveType);
            getEmployeeName(employeeApplyLeaveAddRequest.getName());
            employeeApplyLeave.setDate(new Date());
            employeeApplyLeave.setDay(countDaysBetweenDate(employeeApplyLeaveAddRequest));
            employeeApplyLeaveRepository.save(employeeApplyLeave);
            EmployeeApplyLeaveResponse employeeApplyLeaveResponse= modelMapper.map(employeeApplyLeave,EmployeeApplyLeaveResponse.class);
            return  employeeApplyLeaveResponse;
    }

    @Override
    public List<EmployeeApplyLeaveResponse> getAllEmployeeLeave() throws InvocationTargetException, IllegalAccessException {
        List<EmployeeApplyLeave> employee = employeeApplyLeaveRepository.findAllBySoftDeleteFalse();
        List<EmployeeApplyLeaveResponse> employeeResponse = new ArrayList<>();
        if (!CollectionUtils.isEmpty(employee)) {
            for (EmployeeApplyLeave employeeApplyLeave : employee) {
                EmployeeApplyLeaveResponse employeeApplyLeaveResponse= new EmployeeApplyLeaveResponse();
                nullAwareBeanUtilsBean.copyProperties(employeeApplyLeaveResponse, employeeApplyLeave);
                employeeResponse.add(employeeApplyLeaveResponse);
            }
        }
        return employeeResponse;
    }

    @Override
    public List<EmployeeResponse> getEmployeeByPagination(EmployeeFilter filter, FilterSortRequest.SortRequest<EmployeeSortBy> sort, PageRequest pageRequest) throws InvocationTargetException, IllegalAccessException {
        return employeeRepository.findAllEmployeeByFilterAndSortAndPage(filter, sort, pageRequest);
    }

    @Override
    public EmplDebtResponse addEmployeeDept(EmplAddDebtRequest emplAddDebtRequest, GivenFrom givenFrom, MultipartFile file) {
        EmployeeDebt employeeDebt= modelMapper.map(emplAddDebtRequest,EmployeeDebt.class);
        employeeDebt.setCreatedDate(new Date());
        employeeDebt.setGivenFrom(givenFrom);
        employeeDebtRepository.save(employeeDebt);
        EmplDebtResponse emplDebtResponse= modelMapper.map(employeeDebt,EmplDebtResponse.class);
        return emplDebtResponse;
    }

    //common condition check methods
    public void checkEmployeeDetails(EmployeeRequest employeeRequest) throws InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        if ((StringUtils.isEmpty(employeeRequest.getEmployeeName()) || (employeeRequest.getEmployeeName().matches(adminConfiguration.getNameRegex())))) {
            throw new InvaildRequestException(MessageConstant.INVAILD_EMPLOYEE_NAME);
        }
        if (!employeeRequest.getMobileNo().matches(adminConfiguration.getMoblieNoRegex())) {
            throw new InvaildRequestException(MessageConstant.INVAILD_MOBILENO);
        }
        if (employeeRepository.existsByEmailAndSoftDeleteFalse(employeeRequest.getEmail())) {
            throw new AlreadyExistException(MessageConstant.EMAIL_NAME_EXISTS);
        }
        if (!employeeRequest.getEmail().matches(adminConfiguration.getRegex())) {
            throw new InvaildRequestException(MessageConstant.EMAIL_FORMAT_NOT_VALID);
        }
        if(StringUtils.isEmpty(employeeRequest.getEmail())){
            throw new EmptyException(MessageConstant.EMAIL_EMPTY);
        }
    }

    public void checkApplyLeave(EmployeeApplyLeaveAddRequest employeeApplyLeaveAddRequest,LeaveType leaveType) throws InvocationTargetException, IllegalAccessException {
        AdminConfiguration adminConfiguration = adminService.getConfiguration();
        if ((StringUtils.isEmpty(employeeApplyLeaveAddRequest.getName()) && (employeeApplyLeaveAddRequest.getName().matches(adminConfiguration.getNameRegex())))) {
            throw new InvaildRequestException(MessageConstant.INVAILD_EMPLOYEE_NAME);
        }
        if (StringUtils.isEmpty(employeeApplyLeaveAddRequest.getAmount())) {
            throw new EmptyException(MessageConstant.AMOUNT_NOT_EMPTY);
        }
        if ((StringUtils.isEmpty(employeeApplyLeaveAddRequest.getFromDate()) && (StringUtils.isEmpty(employeeApplyLeaveAddRequest.getToDate())))) {
            throw new EmptyException(MessageConstant.DATE_NOT_EMPTY);
        }
        if (leaveType == null) {
            throw new EmptyException(MessageConstant.LEAVE_TYPE_NOT_EMPTY);
        }
    }

    private Employee getEmployeeName(String name) {
        return employeeRepository.findByEmployeeNameAndSoftDeleteIsFalse(name).orElseThrow(() -> new NotFoundException(MessageConstant.INVAILD_EMPLOYEE_NAME));
    }

    public float countDaysBetweenDate(EmployeeApplyLeaveAddRequest employeeApplyLeaveAddRequest) throws ParseException {
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        Date dateBefore = myFormat.parse(employeeApplyLeaveAddRequest.getFromDate());
        Date dateAfter = myFormat.parse(employeeApplyLeaveAddRequest.getToDate());
        long difference = dateAfter.getTime() - dateBefore.getTime();
        float daysBetween = (difference / (1000*60*60*24));
        return daysBetween;
    }

}
