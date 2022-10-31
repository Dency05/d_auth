package com.example.sm.cc.repository;

import com.example.sm.cc.model.CreditCardModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CreditCardRepository extends MongoRepository<CreditCardModel,String> {
}
