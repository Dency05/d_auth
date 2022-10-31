package com.example.sm.common.repository;

import com.example.sm.common.model.AdminConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdminRepository extends MongoRepository<AdminConfiguration,String> {
}
