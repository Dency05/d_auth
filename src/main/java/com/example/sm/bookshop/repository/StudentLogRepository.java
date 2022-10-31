package com.example.sm.bookshop.repository;

import com.example.sm.bookshop.model.StudentLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentLogRepository extends MongoRepository<StudentLog,String> {
}
