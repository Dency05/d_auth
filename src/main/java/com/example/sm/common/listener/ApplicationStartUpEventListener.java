package com.example.sm.common.listener;

import com.example.sm.auth.controller.AdminConfigurationController;
import com.example.sm.auth.controller.BookShopController;
import com.example.sm.auth.controller.UserController;
import com.example.sm.common.decorator.RestAPI;
import com.example.sm.common.model.AdminConfiguration;
import com.example.sm.common.model.EmailModel;
import com.example.sm.common.repository.AdminRepository;
import com.example.sm.common.repository.RestAPIRepository;
import com.example.sm.common.service.AdminConfigurationService;
import com.example.sm.common.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
@Component
@Slf4j
public class ApplicationStartUpEventListener {
    boolean skip =  false;

    @Autowired
    RestAPIRepository restAPIRepository;
    @Autowired
    AdminRepository adminRepository;
   // @Autowired
    //SchedulerService schedulerService;
    @Autowired
    AdminConfigurationService adminConfigurationService;
    @Autowired
    AdminConfiguration configuration;

    @Autowired
    Utils utils;
   @Autowired
   EmailModel emailModel;


    @EventListener()
    @Async
    public void onApplicationEvent(ContextRefreshedEvent event) throws InvocationTargetException, IllegalAccessException {
        log.debug("Landed in here");
        AdminConfiguration configuration = new AdminConfiguration();
        List<AdminConfiguration> configurations = adminRepository.findAll();
        if(!CollectionUtils.isEmpty(configurations)){
            log.debug("Module Technical configurations exists");
            configuration = configurations.get(0);
        }else {
            configuration = new AdminConfiguration();
            configuration.setCreatedBy("SYSTEM");
            configuration.setCreated(new Date());
            configuration.setUpdatedBy("SYSTEM");
            configuration.setUpdated(new Date());
            configuration = adminRepository.insert(configuration);
            log.debug("Automatically create the module technical configurations");
        }
        // On Application Start up , create the list of authorized services for authorized data
        if(!skip){
            saveIfNotExits(Utils.getAllMethodNames(UserController.class));
            saveIfNotExits(Utils.getAllMethodNames(BookShopController.class));
            saveIfNotExits(Utils.getAllMethodNames(AdminConfigurationController.class));
        }

        Date currentDate = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm (z)");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("CST"));
        String cstTime = dateFormatter.format(currentDate);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String gmtTime = dateFormatter.format(currentDate);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("IST"));
        String istTime = dateFormatter.format(currentDate);

        Set<String> emails = configuration.getTechAdmins();

        /*if (StringUtils.isEmpty(e.getTo())){
            emails.setTo(emails.iterator().next());
        }*/
        /*EmailModel emailModel = new EmailModel();
        AdminConfiguration adminConfiguration= adminConfigurationService.getConfiguration();
        emailModel.setTo(emails.iterator().next());
        emailModel.setCc(adminConfiguration.getTechAdmins());
        //emailModel.setBcc(emailNotificationConfig.getBcc());
        emailModel.setSubject("Auth module started");
        emailModel.setMessage("AuthModule<br/><br/>CST time : "+cstTime+"<br/>GMT time : "+gmtTime+"<br/>IST time : "+istTime);
        utils.sendEmailNow(emailModel);*/
        log.info("Module started mail sent to tech-admins");
        //scheduleCronJobs(adminRepository.findAll().get(0));*/
    }
     public  void saveIfNotExits(List<RestAPI> apis){
        apis.forEach(api->{
            if(!restAPIRepository.existsByName(api.getName())){
                log.info("Added API : {}",api.getName());
                restAPIRepository.insert(api);
            }
        });
    }
    /*
    private void scheduleCronJobs(AdminConfiguration configuration){
        try {
            schedulerService.scheduleCronJob(ModuleCheckScheduleJob.class,configuration.getModuleCheckCronString(), "check_module",null,null);
            List<ComplianceHistory> complianceHistories = historyRepository.findByRunningTrueAndSoftDeleteIsFalse();
            for (ComplianceHistory history : complianceHistories) {
                portfolioService.saveHistory(history);
            }
            //TODO NB Need to stop resident cnc run after fetch data
            schedulerService.scheduleCronJob(ResidentRefreshScheduleJob.class,"0 0 6 1/1 * ? *", "resident_update",null,null);
            //TODO NB Need to stop resident cnc run after fetch data
            schedulerService.scheduleCronJob(ResidentPolicyStateUpdateScheduleJob.class,"0 0 6 1/1 * ? *", "resident_policy_state_update",null,null);
            //Monthly scheduler like 24th
            schedulerService.scheduleCronJob(MonthlyComplianceScheduleJob.class,"0 0 6 1/1 * ? *", "monthly_compliance_calculation",null,null);
//            residentService.calculateRenterChargeDaily();
//            schedulerService.scheduleCronJob(ResidentRenterChargeScheduleJob.class,"0 0 0/1 1/1 * ? *", "resident_renter_charge_calculate",null,null);
            //schedulerService.scheduleOnDate(CommunityInactiveScheduleJob.class,date, "community_inactive_schedule_job");
            log.info("Scheduler job added");
        } catch (SchedulerException e) {
            log.error("Error occurred while creating scheduler job : {}",e.getMessage());
        }
    }*/
}

