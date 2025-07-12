package com.example.kafkauserpoc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendWelcomeEmail(String to, String name, String username) {
        try {
            Context context = new Context();
            context.setVariables(Map.of(
                "name", name,
                "username", username,
                "email", to
            ));

            String emailContent = templateEngine.process("welcome-email", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Bem-vindo ao Sistema!");
            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("Email de boas-vindas enviado com sucesso para: {}", to);
        } catch (MessagingException e) {
            log.error("Erro ao enviar email de boas-vindas para: {}", to, e);
            throw new RuntimeException("Erro ao enviar email", e);
        }
    }
} 