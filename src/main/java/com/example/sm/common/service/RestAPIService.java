package com.example.sm.common.service;

import com.example.sm.common.decorator.RestAPI;

import java.util.List;

public interface RestAPIService {

    List<RestAPI> getAll();

    boolean hasAccess(List<String> roles ,String name);
}
