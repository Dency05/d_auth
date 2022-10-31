package com.example.sm.bookshop.repository;

import com.example.sm.bookshop.model.BookPurchaseLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface BookPurchaseLogRepository extends MongoRepository<BookPurchaseLog,String> {

    Optional<BookPurchaseLog> findByStudentId(String id);
    List<BookPurchaseLog> findByStudentIdAndBookNameAndSoftDeleteIsFalse(String id, String bookName);

    List<BookPurchaseLog> findAllBySoftDeleteFalse();
}
