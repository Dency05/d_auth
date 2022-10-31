package com.example.sm.helper;

import com.example.sm.auth.model.UserModel;
import com.example.sm.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataSetUserHelper {
    @Autowired
    UserHelper userHelper;

    @Autowired
    UserRepository userRepository;

    public void  init(){
      userRepository.saveAll(userHelper.CreateDummyUser());
    }

    public void  cleanUp(){
      userRepository.deleteAll();
    }

    public UserModel getUserModel(){
        return userRepository.findAll().get(0);
    }

}
