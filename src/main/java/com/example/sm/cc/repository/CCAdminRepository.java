package com.example.sm.cc.repository;

import com.example.sm.cc.model.CCAdminConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CCAdminRepository extends MongoRepository<CCAdminConfiguration,String> {

}
