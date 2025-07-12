package com.example.kafkauserpoc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.example.kafkauserpoc.model.EmailMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class JmsEmailService {

    private final EmailService emailService;

    @JmsListener(destination = "${app.jms.queue.email}")
    public void processEmailMessage(EmailMessage message) {
        try {
            log.info("Processando mensagem de email para: {}", message.getTo());
            emailService.sendWelcomeEmail(message.getTo(), message.getName(), message.getUsername());
            log.info("Mensagem de email processada com sucesso para: {}", message.getTo());
        } catch (Exception e) {
            log.error("Erro ao processar mensagem de email para: {}", message.getTo(), e);
            throw e;
        }
    }
} 