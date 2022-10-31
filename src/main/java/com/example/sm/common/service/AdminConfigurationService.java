package com.example.sm.common.service;

import com.example.sm.cc.model.CCAdminConfiguration;
import com.example.sm.common.decorator.AdminResponse;
import com.example.sm.common.model.AdminConfiguration;

import java.lang.reflect.InvocationTargetException;

public interface AdminConfigurationService {
    AdminResponse addConfiguration() throws InvocationTargetException, IllegalAccessException;

    AdminConfiguration getConfiguration() throws InvocationTargetException, IllegalAccessException;

    CCAdminConfiguration getCCAdminDetails() throws InvocationTargetException, IllegalAccessException;
}
