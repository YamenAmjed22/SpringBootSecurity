package com.yamen.security.AdminOnly;

import com.yamen.security.Entity.User;
import com.yamen.security.Repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class adminOnlyService {

    @Autowired
    private  UserRepository userRepository;


    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
