package com.example.sm.bookshop.decorator;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookPurchaseResponses {

    List<BookPurchase> bookPurchases;
    Set<String> title=new LinkedHashSet<>();
    double totalCount;
}
