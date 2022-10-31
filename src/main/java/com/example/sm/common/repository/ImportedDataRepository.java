package com.example.sm.common.repository;

import com.example.sm.common.model.UserImportedData;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface ImportedDataRepository extends MongoRepository<UserImportedData,String> {


}