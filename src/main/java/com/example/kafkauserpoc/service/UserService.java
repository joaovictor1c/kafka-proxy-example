package com.example.kafkauserpoc.service;

import com.example.kafkauserpoc.model.EmailMessage;
import com.example.kafkauserpoc.model.User;
import com.example.kafkauserpoc.repository.UserRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.api.trace.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OutboxService outboxService;
    private final Tracer tracer;
    private final JmsTemplate jmsTemplate;

    @Value("${app.jms.queue.email}")
    private String emailQueue;

    @Transactional
    public User createUser(User user) {
        Span span = tracer.spanBuilder("createUser")
                .setParent(Context.current())
                .setAttribute("user.name", user.getName())
                .setAttribute("user.email", user.getEmail())
                .startSpan();

        try {
            // Salva o usuário
            User savedUser = userRepository.save(user);

            // Cria o evento na tabela outbox
            outboxService.createUserCreatedEvent(
                    savedUser.getId().toString(),
                    savedUser.getEmail(),
                    savedUser.getName()
            );

            // Envia mensagem JMS para envio de email
            EmailMessage emailMessage = new EmailMessage(
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getUsername()
            );
            jmsTemplate.convertAndSend(emailQueue, emailMessage);
            log.info("Mensagem JMS enviada para fila de email: {}", emailQueue);

            span.setStatus(StatusCode.OK);
            span.addEvent("User created successfully with outbox event and email notification");
            return savedUser;
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    public List<User> findAll() {
        Span span = tracer.spanBuilder("findAllUsers")
                .setParent(Context.current())
                .startSpan();
        try {
            List<User> users = userRepository.findAll();
            span.setStatus(StatusCode.OK);
            return users;
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    public Optional<User> findById(Long id) {
        Span span = tracer.spanBuilder("findUserById")
                .setParent(Context.current())
                .setAttribute("user.id", id)
                .startSpan();
        try {
            Optional<User> user = userRepository.findById(id);
            span.setStatus(StatusCode.OK);
            return user;
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
} 