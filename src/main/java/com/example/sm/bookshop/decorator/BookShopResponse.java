package com.example.sm.bookshop.decorator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookShopResponse {
    String bookName;
    String authorName;
    String description;
    String type;
    double price;
    double discount;
}
