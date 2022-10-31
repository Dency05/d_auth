package com.example.sm.helper;

import com.example.sm.bookshop.model.BookPurchaseLog;
import com.example.sm.bookshop.repository.BookPurchaseLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataSetBookPurchaseLogHelper {

    @Autowired
    BookPurchaseLogRepository bookPurchaseLogRepository;

    @Autowired
     BookPurchaseLogHelper bookPurchaseLogHelper;

    public void  init(){
        bookPurchaseLogRepository.saveAll(bookPurchaseLogHelper.createDummyData());
    }

    public void  cleanUp(){
        bookPurchaseLogRepository.deleteAll();
    }

    public BookPurchaseLog getBookShop(){
        return bookPurchaseLogRepository.findAll().get(0);
    }
}
