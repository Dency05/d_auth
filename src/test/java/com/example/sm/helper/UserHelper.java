package com.example.sm.helper;

import com.example.sm.auth.decorator.Result;
import com.example.sm.auth.enums.UserStatus;
import com.example.sm.auth.model.Address;
import com.example.sm.auth.model.UserModel;
import com.example.sm.auth.repository.UserRepository;
import com.example.sm.common.decorator.NullAwareBeanUtilsBean;
import com.example.sm.common.enums.Role;
import com.example.sm.common.utils.PasswordUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Component
public class UserHelper {

    @Autowired
    PasswordUtils passwordUtils;
    List<UserModel> userModels= new ArrayList<>();
    UserModel userModel= new UserModel();
    public List<UserModel> CreateDummyUser(){
       Address address = new Address();
       List<Result> results= new ArrayList<>();

       Result result = new Result();
       userModel.setFullName("Dency S Gevraiya");
       userModel.setFirstName("Dency");
       userModel.setMiddleName("S");
       userModel.setLastName("Gevariya");
       userModel.setUserName("Dency05");
       userModel.setEmail("dency05@gmail.com");
       String password= passwordUtils.encryptPassword("Dency05@1234");
       System.out.println(password);
       userModel.setPassword(password);
       address.setAddress1("Prathana Elegance");
       address.setAddress2("vande matram");
       address.setAddress3("ahemdabad");
       address.setCity("ahmd");
       address.setZipCode("382481");
       userModel.setAddress(address);

       result.setDate(new Date());
       result.setSpi(9.8);
       result.setYear(2022);
       result.setSemester(5);
       results.add(result);
       userModel.setResults(results);
       userModel.setOtp("879543");
       userModel.setDate(new Date());
       userModel.setRole(Role.ANONYMOUS);
       userModel.setUserStatus(UserStatus.INVITED);
       userModel.setCgpi(9.7);
       userModels.add(userModel);
       return userModels;
   }



}
