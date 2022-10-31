package com.example.sm.common.repository;

import com.example.sm.common.model.UserDataModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface UserDataRepository extends MongoRepository<UserDataModel,String> {
  List<UserDataModel> findAllByImportedIdAndSoftDeleteIsFalse(String id);
  boolean existsByEmailAndSoftDeleteFalse(String email);

  Optional<UserDataModel> findByIdAndSoftDeleteIsFalse(String id);

 List<UserDataModel> findByIdInAndSoftDeleteIsFalse(Set<String> userId);
}
