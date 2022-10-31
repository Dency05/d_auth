package com.example.sm.bookshop.decorator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookPurchase {

    String id;
    String month;
    int totalCount;
    List<BookDetails> bookDetails;

}
