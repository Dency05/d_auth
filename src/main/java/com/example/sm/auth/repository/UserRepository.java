package com.example.sm.auth.repository;

import com.example.sm.auth.decorator.UserResponse;
import com.example.sm.auth.enums.UserStatus;
import com.example.sm.auth.model.UserModel;
import com.example.sm.common.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends MongoRepository<UserModel,String> , UserCustomRepository {

    List<UserModel> findAllBySoftDeleteFalse();
    List<UserModel> findByIdInAndSoftDeleteIsFalse(Set<String> ids);
    Optional<UserModel> findByIdAndSoftDeleteIsFalse(String id);

    List<UserModel> findByUserStatusAndSoftDeleteIsFalse(UserStatus userStatus);

    boolean existsByEmailAndSoftDeleteFalse(String email);

    Optional<UserModel> findByEmailAndSoftDeleteIsFalse(String email);

    boolean existsByIdAndOtpAndSoftDeleteFalse(String id,String otp);

    boolean existsByIdAndSoftDeleteFalse(String id);

    Optional<UserModel> findByUserNameAndSoftDeleteIsFalse(String userName);

    List<UserModel> findAllByRoleAndSoftDeleteFalse(Role role);

}
