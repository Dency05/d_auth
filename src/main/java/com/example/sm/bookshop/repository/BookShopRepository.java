package com.example.sm.bookshop.repository;

import com.example.sm.bookshop.model.BookShop;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface BookShopRepository extends MongoRepository<BookShop,String >,BookCustomRepository {
    Optional<BookShop> findByIdAndSoftDeleteIsFalse(String id);

    Optional<BookShop> findByBookNameAndSoftDeleteIsFalse(String name);

}
