package com.example.sm.cc.repository;

import com.example.sm.cc.decorator.CCResponse;
import com.example.sm.cc.decorator.MembershipPlanDetail;
import com.example.sm.cc.enums.MembershipPlan;
import com.example.sm.cc.model.Membership_Logs;
import com.example.sm.common.decorator.CustomAggregationOperation;
import com.example.sm.common.decorator.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
@Slf4j
public class CCCustomRepositoryImpl implements CCCustomRepository {

    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public List<CCResponse> getMembership(MembershipPlan membershipPlan) {
        Criteria criteria = new Criteria();
        List<CCResponse> ccResponses;
        if (membershipPlan != null) {
            criteria = criteria.and("membershipPlan").is(membershipPlan);
        }
        criteria = criteria.and("softDelete").is(false);
        Query query = new Query();
        query.addCriteria(criteria);
        ccResponses = mongoTemplate.find(query,CCResponse.class, "cc_membership");
        System.out.println(ccResponses.size());
        return ccResponses;
    }

    public List<CCResponse> getAllMembership() {
        Criteria criteria = new Criteria();
        criteria = criteria.and("softDelete").is(false);
        criteria = criteria.and("active").is(true);
        Query query = new Query();
        query.addCriteria(criteria);
        List<CCResponse> ccResponses = mongoTemplate.find(query,CCResponse.class, "cc_membership");
        System.out.println(ccResponses.size());
        return ccResponses;
    }
    public List<Membership_Logs> getAllMembership(String id) {
        Criteria criteria = new Criteria();
        criteria = criteria.and("userId").is(id);
        criteria = criteria.and("softDelete").is(false);
        Query query = new Query();
        query.addCriteria(criteria);
        List<Membership_Logs> membership_logs = mongoTemplate.find(query,Membership_Logs.class, "membership_logs");
        System.out.println(membership_logs.size());
        return membership_logs;
    }

    private List<AggregationOperation> getmembership() throws JSONException {
        List<AggregationOperation> operations = new ArrayList<>();
        String fileName= FileReader.loadFile("aggregation/membershipPlan.json");
        JSONObject jsonObject= new JSONObject(fileName);
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"softDelete",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"membershipPlan",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"planNames",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"groupPlanName",Object.class))));
        operations.add(new CustomAggregationOperation(Document.parse(CustomAggregationOperation.getJson(jsonObject,"sorting",Object.class))));
        return operations;
    }

    public List<MembershipPlanDetail> getMembershipPlan() throws JSONException {
        List<AggregationOperation> operations = getmembership();
        Aggregation aggregation = newAggregation(operations);
        return mongoTemplate.aggregate(aggregation, "cc_membership",MembershipPlanDetail.class).getMappedResults();
    }

}
