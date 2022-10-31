package com.example.sm.auth.rabbitmq;

import com.example.sm.auth.model.UserModel;
import com.example.sm.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@Slf4j
public class UserConsumerServiceImpl implements  UserConsumerService {

    @Autowired
    UserRepository userRepository;
    @Override
    public String getUser(String id) {
        Optional<UserModel> userModel = userRepository.findByIdAndSoftDeleteIsFalse(id);
        log.info("userModel: {}",userModel);
        return userModel.toString();

    }
}
