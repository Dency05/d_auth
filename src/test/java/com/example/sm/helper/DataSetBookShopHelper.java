package com.example.sm.helper;

import com.example.sm.auth.model.UserModel;
import com.example.sm.bookshop.model.BookShop;
import com.example.sm.bookshop.repository.BookShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataSetBookShopHelper {
    @Autowired
    BookShopHelper bookShopHelper;

    @Autowired
    BookShopRepository bookShopRepository;

    public void  init(){
        bookShopRepository.saveAll(bookShopHelper.createDummyData());
    }

    public void  cleanUp(){
        bookShopRepository.deleteAll();
    }

    public BookShop getBookShop(){
        return bookShopRepository.findAll().get(0);
    }
}
