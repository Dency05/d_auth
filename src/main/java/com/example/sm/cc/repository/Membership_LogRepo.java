package com.example.sm.cc.repository;

import com.example.sm.cc.model.Membership_Logs;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface Membership_LogRepo extends MongoRepository<Membership_Logs,String> {
    Optional<Membership_Logs> findByUserIdAndSoftDeleteIsFalse(String id);
}
