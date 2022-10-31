package com.example.sm.auth.controller;

import com.example.sm.auth.decorator.MonthTitleName;
import com.example.sm.cc.decorator.*;
import com.example.sm.cc.enums.MembershipPlan;
import com.example.sm.cc.enums.PaymentOption;
import com.example.sm.cc.model.CCUser;
import com.example.sm.cc.model.ChapterName;
import com.example.sm.cc.model.Membership_Logs;
import com.example.sm.cc.service.CCService;
import com.example.sm.common.decorator.DataResponse;
import com.example.sm.common.decorator.ListResponse;
import com.example.sm.common.decorator.Response;
import com.example.sm.common.enums.Role;
import com.example.sm.common.utils.Access;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cc")
public class CommunityConnectController {

    @Autowired
    CCService ccService;

    @SneakyThrows
    @RequestMapping(name = "addOrUpdateMembershipPlan", value = "/add", method = RequestMethod.POST)
    public DataResponse<CCResponse> addOrUpdateMembershipPlan (@RequestParam(required = false) String id,@RequestBody CCAddRequest ccAddRequest) {
        DataResponse<CCResponse> dataResponse = new DataResponse<>();
        dataResponse.setData(ccService.addOrUpdateMembershipPlan(id,ccAddRequest));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "getAllMemberShip", value = "/getAll", method = RequestMethod.GET)
    public ListResponse<CCResponse> getAllMemberShip() {
        ListResponse<CCResponse> listResponse = new ListResponse<>();
        listResponse.setData(ccService.getAllMembership());
        listResponse.setStatus(Response.getSuccessResponse());
        return listResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "addOrUpdateChapterName", value = "/add/chapterName", method = RequestMethod.POST)
    public DataResponse<ChapterName> addOrUpdateChapterName (@RequestBody ChapterNameAddRequest chapterNameAddRequest,@RequestParam String id) {
        DataResponse<ChapterName> dataResponse = new DataResponse<>();
        dataResponse.setData(ccService.addOrUpdateChapterName(chapterNameAddRequest,id));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @RequestMapping(name = "getMembershipPlan", value = "get/membership", method = RequestMethod.POST)
    @Access(levels = Role.ANONYMOUS)
    public ListResponse<CCResponse> getMembershipPlan(@RequestParam MembershipPlan membershipPlan) {
        ListResponse<CCResponse> listResponse = new ListResponse<>();
        listResponse.setData(ccService.getMembershipPlan(membershipPlan));
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }

    @RequestMapping(name = "getChapterName", value = "get/chapterName", method = RequestMethod.GET)
    @Access(levels = Role.ANONYMOUS)
    public ListResponse<ChapterName> getChapterName(@RequestParam MembershipPlan membershipPlan) {
        ListResponse<ChapterName> listResponse = new ListResponse<>();
        listResponse.setData(ccService.getChapterName(membershipPlan));
        listResponse.setStatus(Response.getOkResponse());
        return listResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "addPayment", value = "/add/Payment", method = RequestMethod.POST)
    public DataResponse<Object> addPayment (@RequestParam PaymentOption paymentOption, @RequestBody CreditCardRequest creditCardRequest) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        dataResponse.setData(ccService.addPayment(paymentOption,creditCardRequest));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "addE_CheckPayment", value = "/add/E-check/payment", method = RequestMethod.POST)
    public DataResponse<Object> addE_CheckPayment (@RequestParam PaymentOption paymentOption, @RequestBody E_CheckRequest e_checkRequest) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        ccService.addE_CheckPayment(paymentOption,e_checkRequest);
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @Access(levels = {Role.ADMIN})
    @RequestMapping(name = "deleteMembership", value = "/delete{id}", method = RequestMethod.GET)
    public DataResponse<Object> deleteMembership(@RequestParam String id) {
        DataResponse<Object> dataResponse = new DataResponse<>();
        ccService.deleteMembership(id);
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }
    @SneakyThrows
    @RequestMapping(name = "addOrUpdateUser", value = "/addUser", method = RequestMethod.POST)
    public DataResponse<CCUser> addOrUpdateUser (@RequestParam(required = false) String id, @RequestBody CCUserAddRequest ccUserAddRequest) {
        DataResponse<CCUser> dataResponse = new DataResponse<>();
        dataResponse.setData(ccService.addOrUpdateUser(id,ccUserAddRequest));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "addMembershipLogs", value = "/add/membership/logs", method = RequestMethod.POST)
    public DataResponse<Membership_Logs> addMembershipLogs (@RequestBody Membership_LogsAddRequest membership_logsAddRequest) {
        DataResponse<Membership_Logs> dataResponse = new DataResponse<>();
        dataResponse.setData(ccService.addMembershipLogs(membership_logsAddRequest));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @RequestMapping(name="saveUser",value = "save/user",method=RequestMethod.GET)
    public DataResponse<CCUser> saveUser (@RequestParam String id) {
        DataResponse<CCUser> dataResponse = new DataResponse<>();
        dataResponse.setData(ccService.saveUser(id));
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "getMembershipPlans", value = "get/membershipPlans", method = RequestMethod.GET)
    public DataResponse<MembershipName> getMembershipPlans() {
        DataResponse<MembershipName> dataResponse = new DataResponse<>();
        dataResponse.setData(ccService.getMembershipPlans());
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }
}
