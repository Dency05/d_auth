package com.example.sm.cc.repository;

import com.example.sm.cc.decorator.CCResponse;
import com.example.sm.cc.enums.MembershipPlan;
import com.example.sm.cc.model.ChapterName;
import com.example.sm.common.model.UserDataModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChapterNameRepository extends MongoRepository<ChapterName,String> {

    List<ChapterName> findByMembershipPlansInAndSoftDeleteIsFalse(MembershipPlan membershipPlan);

    Optional<ChapterName> findByIdAndSoftDeleteIsFalse(String id);
}
