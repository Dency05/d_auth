package com.example.sm.auth.decorator;

import com.example.sm.auth.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseExcel {

    String id;
    String firstName;
    String middleName;
    String lastName;
    int age;
    String email;
    String userName;
    String mobileNo;
    Address address;


    @ExcelField(excelHeader="Id",position = 1)
    public  String getId(){
        return  id;
    }
    @ExcelField(excelHeader="FirstName",position = 2)
    public  String getFirstName(){
        return firstName;
    }
    @ExcelField(excelHeader="MiddleName",position = 3)
    public String getMiddleName(){
        return middleName;
    }
    @ExcelField(excelHeader="LastName",position = 4)
    public String getLastName(){
        return lastName;
    }
    @ExcelField(excelHeader="Email",position = 5)
    public String getEmail(){
        return email;
    }
    @ExcelField(excelHeader="UserName",position = 6)
    public String getUserName(){
        return userName;
    }
    @ExcelField(excelHeader="MobileNo",position = 7)
    public String getMobileNo(){
        return mobileNo;
    }

}
