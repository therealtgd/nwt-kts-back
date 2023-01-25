package com.foober.foober.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import com.foober.foober.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendRegistrationEmail(Client client, String token) throws MessagingException, IOException {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("name", client.getDisplayName());
        variables.put("link", "http://localhost:4200/confirm-registration/" + token);

        sendEmail(variables, "Registration Confirmation", client.getEmail());
    }

    private void sendEmail(HashMap<String, String> variables, String subject, String sendTo) throws MessagingException, IOException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setText(buildEmailFromTemplate(variables), true);
        helper.setTo(sendTo);
        helper.setSubject(subject);
        helper.setFrom("gajba.na.vodi@gmail.com");
        mailSender.send(mimeMessage);

    }

    private String buildEmailFromTemplate(HashMap<String, String> variables) throws IOException {
        String message = getResourceFileAsString("emailTemplates/clientRegistration.html");

        String target;
        String value;

        for (HashMap.Entry<String, String> entry : variables.entrySet()) {
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
