package com.example.sm.bookshop.decorator;

import com.example.sm.auth.decorator.ExcelField;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor

public class BookDataExcel {
    int price;
    String date;
    String bookName;
    int totalCount;

    @ExcelField(excelHeader="Book Name",position = 2)
    public  String getBookName(){
        return bookName;
    }

    @ExcelField(excelHeader="Date",position = 3)
    public  String getDate(){
        return date;
    }

    @ExcelField(excelHeader="Price",position = 4)
    public int getPrice(){
        return price;
    }

    @ExcelField(excelHeader="Total Count",position = 5)
    public int getTotalCount(){
        return totalCount;
    }

}
