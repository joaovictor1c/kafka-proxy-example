package com.example.kafkauserpoc.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedEventListener {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "events.UserCreated", groupId = "user-created-group")
    public void listen(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            JsonNode payload = event.get("payload");
            
            String userId = payload.get("userId").asText();
            String email = payload.get("email").asText();
            String name = payload.get("name").asText();

            log.info("Evento UserCreated recebido - ID: {}, Nome: {}, Email: {}", userId, name, email);
            log.info("Evento processado com sucesso");
        } catch (Exception e) {
            log.error("Erro ao processar evento UserCreated: {}", message, e);
        }
    }
} 