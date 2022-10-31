package com.example.sm.bookshop.repository;

import com.example.sm.bookshop.decorator.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface StudentRepository extends MongoRepository<Student,String> {
    Optional<Student> findByIdAndSoftDeleteIsFalse(String id);
}
