package com.example.sm.cc.service;

import com.example.sm.cc.decorator.*;
import com.example.sm.cc.enums.MembershipPlan;
import com.example.sm.cc.enums.PaymentOption;
import com.example.sm.cc.model.CCUser;
import com.example.sm.cc.model.ChapterName;
import com.example.sm.cc.model.Membership_Logs;
import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface CCService {
    CCResponse addOrUpdateMembershipPlan(String id,CCAddRequest ccAddRequest) throws InvocationTargetException, IllegalAccessException;

    ChapterName addOrUpdateChapterName(ChapterNameAddRequest chapterNameAddRequest,String id) throws InvocationTargetException, IllegalAccessException;

    List<CCResponse> getMembershipPlan(MembershipPlan membershipPlan);

    List<ChapterName> getChapterName(MembershipPlan membershipPlan);


    String addPayment(PaymentOption paymentOption, CreditCardRequest creditCardRequest) throws InvocationTargetException, IllegalAccessException;

    List<CCResponse> getAllMembership() throws InvocationTargetException, IllegalAccessException;

    void deleteMembership(String id);

    void addE_CheckPayment(PaymentOption paymentOption, E_CheckRequest e_checkRequest);

    CCUser addOrUpdateUser(String id, CCUserAddRequest ccUserAddRequest);

    Membership_Logs addMembershipLogs(Membership_LogsAddRequest membership_logsAddRequest);

    CCUser saveUser(String id);

    MembershipName getMembershipPlans() throws JSONException, InvocationTargetException, IllegalAccessException;
}
