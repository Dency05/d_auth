package com.example.sm.bookshop.decorator;

import com.example.sm.auth.decorator.ExcelField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookPurchasedDetailByMonthInExcel {
    String studentName;
    String bookName;
    String studentId;
    int totalPrice;
    int count;
    int price;
    int month;

    @ExcelField(excelHeader="Student Name",position = 2)
    public  String getStudentName(){
        return studentName;
    }

    @ExcelField(excelHeader="Book Name",position = 3)
    public  String getBookName(){
        return bookName;
    }

    @ExcelField(excelHeader="Student Id",position = 4)
    public  String getStudentId(){
        return studentId;
    }

    @ExcelField(excelHeader="Total Price",position = 5)
    public  int getTotalPrice(){
        return totalPrice;
    }

    @ExcelField(excelHeader="Book Cunt",position = 6)
    public  int getCount(){
        return count;
    }

    @ExcelField(excelHeader="Price",position = 7)
    public  int getPrice(){
        return price;
    }
}
