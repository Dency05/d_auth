package com.example.sm.bookshop.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDetail {
    String date;
    String bookName;
    int price;
    int totalCount;
}
