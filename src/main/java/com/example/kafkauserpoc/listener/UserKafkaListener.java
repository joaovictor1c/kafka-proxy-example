package com.example.kafkauserpoc.listener;

import com.example.kafkauserpoc.model.User;
import com.example.kafkauserpoc.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserKafkaListener {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic}")
    private String topic;

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        log.info("Mensagem recebida: {}", message);
        try {
            User user = objectMapper.readValue(message, User.class);
            userService.saveUser(user);
            log.info("Usuário salvo com sucesso: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Erro ao processar a mensagem: {}", message, e);
        }
    }
} 