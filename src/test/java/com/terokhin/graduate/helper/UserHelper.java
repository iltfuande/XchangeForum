package com.terokhin.graduate.helper;

import com.terokhin.graduate.model.enity.User;
import com.terokhin.graduate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserHelper {

    @Autowired
    private UserRepository userRepository;

    public User insertUser(User newUser) {
        return userRepository.save(newUser);
    }
}