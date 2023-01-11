package com.foober.foober.service;

import com.foober.foober.model.Client;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmailService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender emailSender;

    @Async
    public void sendRegistrationEmail(Client client, String token) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("name", client.getFirstName());
        variables.put("link", "http://localhost:4200/confirm-registration/" + token);

        sendEmail(variables, "Registration Confirmation", client.getUsername());
    }

    private void sendEmail(Map<String, String> variables, String subject, String sendTo) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setText(buildEmailFromTemplate(variables), true);
            helper.setTo(sendTo);
            helper.setSubject(subject);
            helper.setFrom("gajba.na.vodi@gmail.com");
            emailSender.send(mimeMessage);

        } catch (MessagingException | IOException e) {
            LOGGER.error("Failed to send email", e);
        }
    }

    private String buildEmailFromTemplate(Map<String, String> variables) throws IOException {
        String message = getResourceFileAsString("emailTemplates/resource.html");

        String target;
        String value;

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            target = "\\{\\{ " + entry.getKey() + " \\}\\}";
            value = entry.getValue();

            message = message.replaceAll(target, value);
        }
        return message;
    }
    public static String getResourceFileAsString(String fileName) {
        InputStream is = getResourceFileAsInputStream(fileName);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return (String)reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } else {
            throw new RuntimeException("resource not found");
        }
    }

    public static InputStream getResourceFileAsInputStream(String fileName) {
        ClassLoader classLoader = EmailService.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }
}
