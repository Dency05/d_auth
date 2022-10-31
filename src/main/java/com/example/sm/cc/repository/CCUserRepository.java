package com.example.sm.cc.repository;

import com.example.sm.cc.model.CCUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CCUserRepository extends MongoRepository<CCUser,String> {

    Optional<CCUser> findByIdAndSoftDeleteIsFalse(String id);
}
