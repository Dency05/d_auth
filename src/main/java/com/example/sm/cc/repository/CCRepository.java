package com.example.sm.cc.repository;

import com.example.sm.cc.model.CCModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CCRepository extends MongoRepository<CCModel,String>, CCCustomRepository {
    Optional<CCModel> findByIdAndSoftDeleteIsFalse(String id);
    List<CCModel> findAllBySoftDeleteFalseAndActiveTrue();

    CCModel findByMembershipNameAndSoftDeleteIsFalse(String memberShipName);
}
