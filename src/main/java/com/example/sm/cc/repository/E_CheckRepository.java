package com.example.sm.cc.repository;

import com.example.sm.cc.model.E_CheckModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface E_CheckRepository extends MongoRepository<E_CheckModel,String> {
}
