package com.example.sm.bookshop.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    String studentId;
    String studentName;
    String bookName;
    Date date;
    double price;
}
