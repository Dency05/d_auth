package com.example.sm.cc.repository;

import com.example.sm.cc.decorator.CCResponse;
import com.example.sm.cc.decorator.MembershipPlanDetail;
import com.example.sm.cc.enums.MembershipPlan;
import com.example.sm.cc.model.Membership_Logs;
import org.json.JSONException;

import java.util.List;

public interface CCCustomRepository {

    List<CCResponse> getMembership(MembershipPlan membershipPlan);
    List<CCResponse> getAllMembership();
    List<Membership_Logs> getAllMembership(String id);
    List<MembershipPlanDetail> getMembershipPlan() throws JSONException;
}
