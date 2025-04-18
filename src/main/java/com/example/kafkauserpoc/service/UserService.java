package com.example.kafkauserpoc.service;

import com.example.kafkauserpoc.model.User;
import com.example.kafkauserpoc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    public User saveUser(User user) {
        log.info("Salvando usuário: {}", user.getUsername());
        return userRepository.save(user);
    }
} 