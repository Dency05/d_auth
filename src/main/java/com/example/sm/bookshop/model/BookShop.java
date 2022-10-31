package com.example.sm.bookshop.model;

import com.example.sm.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "book_shop")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookShop {
    @Id
    String id;
    String bookName;
    String authorName;
    String description;
    String type;
    double price;
    double discount;
    double reSaleDiscount;
    double afterReSalePrice;
    Date date;
    Role role;
    double afterDiscountPrice;

    @JsonIgnore
    boolean softDelete= false;

}
