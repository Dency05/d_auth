package com.example.sm.cc.service;

import com.example.sm.cc.decorator.*;
import com.example.sm.cc.enums.MembershipPlan;
import com.example.sm.cc.enums.PaymentOption;
import com.example.sm.cc.model.*;
import com.example.sm.cc.repository.*;
import com.example.sm.common.constant.MessageConstant;
import com.example.sm.common.decorator.NullAwareBeanUtilsBean;
import com.example.sm.common.exception.InvaildRequestException;
import com.example.sm.common.exception.NotFoundException;

import com.example.sm.common.model.AdminConfiguration;
import com.example.sm.common.service.AdminConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CCServiceImpl implements CCService {

    @Autowired
    CCRepository ccRepository;

    @Autowired
    Membership_LogRepo membership_logRepo;
    @Autowired
    CCUserRepository ccUserRepository;

    @Autowired
    CreditCardRepository creditCardRepository;

    @Autowired
    E_CheckRepository e_checkRepository;
    @Autowired
    ChapterNameRepository chapterNameRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AdminConfigurationService adminService;
    @Autowired
    NullAwareBeanUtilsBean nullAwareBeanUtilsBean;

    @Override
    public CCResponse addOrUpdateMembershipPlan(String id,CCAddRequest ccAddRequest) throws InvocationTargetException, IllegalAccessException {
        if (id != null) {
            CCModel ccModel= getCCModel(id);
            nullAwareBeanUtilsBean.copyProperties(ccModel,ccAddRequest);
            /*if (ccAddRequest.getBenefits().isEmpty()){
                ccModel.setBenefits(null);
            }*/
            ccRepository.save(ccModel);
            return modelMapper.map(ccModel,CCResponse.class);
        }
        else {
            CCModel ccModel = modelMapper.map(ccAddRequest, CCModel.class);
            /*if (!CollectionUtils.isEmpty(ccModel.getMembershipPlan())){
                for (MembershipPlans membershipPlans : ccModel.getMembershipPlan()) {
                    membershipPlans.setDate(new Date());
                    //membershipPlans.setActive(true);
                }
            }*/
            ccModel.setDate(new Date());
            ccModel.setActive(true);
            ccRepository.save(ccModel);
            return modelMapper.map(ccModel, CCResponse.class);
        }
    }
    @Override
    public ChapterName addOrUpdateChapterName(ChapterNameAddRequest chapterNameAddRequest, String id) throws InvocationTargetException, IllegalAccessException {
        if (id != null) {
            ChapterName chapterName= getChapterName(id);
            nullAwareBeanUtilsBean.copyProperties(chapterName, chapterNameAddRequest);
            chapterNameRepository.save(chapterName);
            return chapterName;
        }
        ChapterName chapterName = modelMapper.map(chapterNameAddRequest, ChapterName.class);
        chapterNameRepository.save(chapterName);
        return chapterName;
    }

    @Override
    public List<CCResponse> getMembershipPlan(MembershipPlan membershipPlan) {
        return ccRepository.getMembership(membershipPlan);
    }
    @Override
    public List<ChapterName> getChapterName(MembershipPlan membershipPlan) {
        List<ChapterName> chapterNames = chapterNameRepository.findByMembershipPlansInAndSoftDeleteIsFalse(membershipPlan);
        return chapterNames;
    }

    @Override
    public String addPayment(PaymentOption paymentOption, CreditCardRequest creditCardRequest) throws InvocationTargetException, IllegalAccessException {
       if(paymentOption == (PaymentOption.CREDIT_CARD)){
           CreditCardModel creditCardModel= new CreditCardModel();
           nullAwareBeanUtilsBean.copyProperties(creditCardModel,creditCardRequest);
           System.out.printf("creditmodel"+creditCardModel);
           creditCardRepository.save(creditCardModel);
       }
       else{
          throw new InvaildRequestException(MessageConstant.INVAILD_PAYMENT_OPTION);
       }
      return "Information Added Successfully...";
    }

    @Override
    public List<CCResponse> getAllMembership() throws InvocationTargetException, IllegalAccessException {
        List<CCModel> ccModels= ccRepository.findAllBySoftDeleteFalseAndActiveTrue();
        log.info("CCmodel is: {}",ccModels);
        List<CCResponse> ccResponses= new ArrayList<>();
        if (!CollectionUtils.isEmpty(ccModels)) {
            for (CCModel ccModel : ccModels) {
                CCResponse ccResponse = new CCResponse();
                nullAwareBeanUtilsBean.copyProperties(ccResponse, ccModel);
                ccResponses.add(ccResponse);
            }
        }
        return ccResponses;
    }

    @Override
    public void deleteMembership(String id) {
        CCModel ccModel= getCCModel(id);
        ccModel.setSoftDelete(true);
        ccModel.setActive(true);
        ccRepository.save(ccModel);
    }

    @Override
    public void addE_CheckPayment(PaymentOption paymentOption, E_CheckRequest e_checkRequest) {
        if(paymentOption==(PaymentOption.E_CHECK)){
            E_CheckModel e_checkModel= modelMapper.map(e_checkRequest,E_CheckModel.class);
            log.info("echeck :{}",e_checkModel);
            e_checkRepository.save(e_checkModel);
        }
    }

    @Override
    public CCUser addOrUpdateUser(String id, CCUserAddRequest ccUserAddRequest) {
        CCUser ccUser = modelMapper.map(ccUserAddRequest, CCUser.class);
        ccUserRepository.save(ccUser);
        return ccUser;
    }

    @Override
    public Membership_Logs addMembershipLogs(Membership_LogsAddRequest membership_logsAddRequest) {
        Membership_Logs membership_logs = modelMapper.map(membership_logsAddRequest, Membership_Logs.class);
        CCModel ccModel = ccRepository.findByMembershipNameAndSoftDeleteIsFalse(membership_logsAddRequest.getMembershipName());
        log.info("id:{}", ccModel.getId());
        List<MembershipPlans> ccModels = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ccModel.getMembershipPlan())) {
            ccModels = ccModel.getMembershipPlan();
        }
        log.info("ccmodels:{}", ccModels);
        for (MembershipPlans model : ccModels) {
            log.info("inside for:{}", model.getMembershipPlan());
            if (membership_logsAddRequest.getMembershipPlanName().equals(model.getMembershipPlan().toString())) {
                log.info("inside if:{}", model.getMembershipPlan());
                membership_logs.setAmount(model.getAmount());
                membership_logs.setDate(model.getDate());
                membership_logs.setMembershipPlanId(ccModel.getId());
            }
        }
        membership_logRepo.save(membership_logs);
        return membership_logs;
    }

    @Override
    public CCUser saveUser(String id) {
      List<Membership_Logs> membership_logs = ccRepository.getAllMembership(id);
      CCUser ccUser= getCCUser(id);
      Set<String> planIds = new HashSet<>();
      if (!CollectionUtils.isEmpty(ccUser.getPlanIds())){
          planIds = ccUser.getPlanIds();
      }
      Set<String> newPlanIds = membership_logs.stream().map(Membership_Logs::getId).collect(Collectors.toSet());
      planIds.addAll(newPlanIds);
      ccUser.setPlanIds(planIds);
      ccUserRepository.save(ccUser);
      return ccUser;
    }

    @Override
    public MembershipName getMembershipPlans() throws JSONException, InvocationTargetException, IllegalAccessException {
     AdminConfiguration adminConfiguration = adminService.getConfiguration();
     List<MembershipPlanDetail> membershipPlanDetails= new ArrayList<>(ccRepository.getMembershipPlan());
     MembershipName membershipName= new MembershipName();
     double totalCount=0;
     Set<String> titles= new LinkedHashSet<>();

       for (Map.Entry<Integer,String> entry : adminConfiguration.getMembershipTitle().entrySet()) {
            String titleName = entry.getValue();
            titles.add(titleName);
            boolean exist = membershipPlanDetails.stream().anyMatch(e -> e.getPlanName().equals(entry.getValue()));
            if(!exist){ // check month is null or not if null then set count 0 and set month name
                MembershipPlanDetail membershipPlanDetail = new MembershipPlanDetail();
                membershipPlanDetail.setCount(0.0);
                membershipPlanDetail.setPlanName(entry.getValue());
                membershipPlanDetails.add(membershipPlanDetail);
            }
        }
      membershipName.setTitles(titles);
      membershipName.setMembershipPlanDetails(membershipPlanDetails);
        for (MembershipPlanDetail membershipPlanDetail : membershipPlanDetails) {
            totalCount= totalCount + membershipPlanDetail.getCount();
            membershipName.setTotalCount(totalCount);
        }
        return membershipName;
    }

    private CCModel getCCModel(String id) {
        return ccRepository.findByIdAndSoftDeleteIsFalse(id).orElseThrow(() -> new NotFoundException(MessageConstant.USER_ID_NOT_FOUND));
    }
    private ChapterName getChapterName(String id) {
        return chapterNameRepository.findByIdAndSoftDeleteIsFalse(id).orElseThrow(() -> new NotFoundException(MessageConstant.USER_ID_NOT_FOUND));
    }
    private CCUser getCCUser(String id) {
        return ccUserRepository.findByIdAndSoftDeleteIsFalse(id).orElseThrow(() -> new NotFoundException(MessageConstant.USER_ID_NOT_FOUND));
    }
}
