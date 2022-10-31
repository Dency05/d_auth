package com.example.sm.helper;

import com.example.sm.bookshop.model.BookPurchaseLog;
import com.example.sm.common.enums.Role;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class BookPurchaseLogHelper {
    List<BookPurchaseLog>  bookPurchaseLogs= new ArrayList<>();
    BookPurchaseLog bookPurchaseLog= new BookPurchaseLog();
    List<BookPurchaseLog> createDummyData(){
        bookPurchaseLog.setStudentName("Dency");
        bookPurchaseLog.setBookName("hear Your self");
        bookPurchaseLog.setBalance(100.00);
        bookPurchaseLog.setStudentId("63049e50d6c35e04daa00ef8");
        bookPurchaseLog.setDate(new Date());
        bookPurchaseLog.setPrice(50);
        bookPurchaseLog.setRole(Role.ADMIN);
        bookPurchaseLogs.add(bookPurchaseLog);
        return bookPurchaseLogs;
    }
}

