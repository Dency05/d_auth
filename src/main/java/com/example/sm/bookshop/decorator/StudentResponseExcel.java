package com.example.sm.bookshop.decorator;

import com.example.sm.auth.decorator.ExcelField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseExcel {
    String studentId;
    String studentName;
    double price;
    String date;


    @ExcelField(excelHeader="studentId",position = 2)
    public  String getStudentId(){
        return studentId;
    }

    @ExcelField(excelHeader="studentName",position = 3)
    public  String getStudentName(){
        return studentName;
    }

    @ExcelField(excelHeader="price",position = 5)
    public  double getPrice(){
        return price;
    }

    @ExcelField(excelHeader="date",position = 4)
    public  String getDate(){
        return date;
    }

}
