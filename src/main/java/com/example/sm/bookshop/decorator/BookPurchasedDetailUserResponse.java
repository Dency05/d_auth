package com.example.sm.bookshop.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookPurchasedDetailUserResponse {

    String studentName;
    String bookName;
    String studentId;
    int totalPrice;
    int count;
    int price;
    int month;
}
