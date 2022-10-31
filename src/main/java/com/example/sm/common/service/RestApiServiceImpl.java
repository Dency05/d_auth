package com.example.sm.common.service;

import com.example.sm.common.decorator.RestAPI;
import com.example.sm.common.repository.RestAPIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
@Repository
public class RestApiServiceImpl implements RestAPIService {

    @Autowired
    RestAPIRepository restApiRepository;

    @Override
    public List<RestAPI> getAll() {
        List<RestAPI> restAPIS = restApiRepository.findAllBy();
        return restAPIS;
    }

    @Override
    public boolean hasAccess(List<String> roles,String name) {
        return restApiRepository.existsByRolesInAndName(roles,name);
    }
}