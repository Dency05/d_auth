package com.example.sm.helper;


import com.example.sm.bookshop.model.BookShop;
import com.example.sm.common.enums.Role;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class BookShopHelper {

    List<BookShop> bookShops= new ArrayList<>();
    BookShop bookShop= new BookShop();

    List<BookShop> createDummyData(){
        bookShop.setBookName("Hear YourSelf");
        bookShop.setDate(new Date());
        bookShop.setDescription("djnjnkk");
        bookShop.setAfterDiscountPrice(95.0);
        bookShop.setDiscount(0.05);
        bookShop.setPrice(100.0);
        bookShop.setReSaleDiscount(40.0);
        bookShop.setAfterReSalePrice(50.0);
        bookShop.setType("gjhjj");
        bookShop.setRole(Role.ADMIN);
        bookShops.add(bookShop);
        return bookShops;
    }
}
