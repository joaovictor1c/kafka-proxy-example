package com.example.kafkauserpoc.service;

import com.example.kafkauserpoc.model.OutboxEvent;
import com.example.kafkauserpoc.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void createUserCreatedEvent(String aggregateId, String email, String name) {
        try {
            String payload = objectMapper.writeValueAsString(new UserPayload(
                    aggregateId,
                    email,
                    name
            ));

            OutboxEvent event = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType("User")
                    .aggregateId(aggregateId)
                    .eventType("UserCreated")
                    .payload(payload)
                    .build();

            outboxEventRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Error creating outbox event", e);
        }
    }

    private static class UserPayload {
        private final String userId;
        private final String email;
        private final String name;

        public UserPayload(String userId, String email, String name) {
            this.userId = userId;
            this.email = email;
            this.name = name;
        }

        public String getUserId() {
            return userId;
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }
    }
} 