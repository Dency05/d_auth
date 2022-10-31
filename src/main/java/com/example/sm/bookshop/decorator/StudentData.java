package com.example.sm.bookshop.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class StudentData {
    String studentName;
    String bookName;
    String studentId;
    int totalPrice;
    int bookCount;
    int price;
}
