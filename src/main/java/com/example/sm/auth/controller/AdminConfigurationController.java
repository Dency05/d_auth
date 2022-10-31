package com.example.sm.auth.controller;

import com.example.sm.cc.model.CCAdminConfiguration;
import com.example.sm.common.service.AdminConfigurationService;
import com.example.sm.common.decorator.AdminResponse;
import com.example.sm.common.decorator.DataResponse;
import com.example.sm.common.decorator.Response;
import com.example.sm.common.model.AdminConfiguration;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/configuration")

public class AdminConfigurationController {
    @Autowired
    AdminConfigurationService adminService;

    @SneakyThrows
    @RequestMapping(name = "addconfigurtaion",value = "/add",method = RequestMethod.GET)
    public DataResponse<AdminResponse> addConfiguration(){
        DataResponse<AdminResponse> dataResponse=new DataResponse<>();
        dataResponse.setData(adminService.addConfiguration());
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "getConfigurtaionDetails",value = "/getDetail",method = RequestMethod.GET)
    public DataResponse<AdminConfiguration> getConfigurationDetail(){
        DataResponse<AdminConfiguration> dataResponse=new DataResponse<>();
        dataResponse.setData(adminService.getConfiguration());
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }

    @SneakyThrows
    @RequestMapping(name = "getCCAdminDetails",value = "/get/cc/admin/detail",method = RequestMethod.GET)
    public DataResponse<CCAdminConfiguration> getCCAdminDetails(){
        DataResponse<CCAdminConfiguration> dataResponse=new DataResponse<>();
        dataResponse.setData(adminService.getCCAdminDetails());
        dataResponse.setStatus(Response.getOkResponse());
        return dataResponse;
    }
}
