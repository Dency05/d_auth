package com.example.sm.bookshop.model;

import com.example.sm.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bookPurchaseLog")
@Builder
public class BookPurchaseLog {
    @Id
    String id;
    String studentId;
    String bookName;
    String bookId;
    String studentName;
    double price;
    double balance;
    Date date;
    Role role;
    boolean softDelete= false;
}
