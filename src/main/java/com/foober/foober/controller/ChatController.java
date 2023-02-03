package com.foober.foober.controller;

import com.foober.foober.dto.ChatMessage;
import com.foober.foober.model.User;
import com.foober.foober.security.jwt.TokenProvider;
import com.foober.foober.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

@Controller
@AllArgsConstructor
public class ChatController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/support-chat/send")
    public void sendMessage(@Header(name = "Authorization") String authorization, Message<ChatMessage> message) {
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            if (this.tokenProvider.validateToken(token)) {
                Long userId = tokenProvider.getUserIdFromToken(token);
                User user = this.userService.findById(userId).orElseThrow();
                this.simpMessagingTemplate.convertAndSend(
                    "/support-chat/admin",
                    new ChatMessage(user.getUsername(), message.getPayload().content)
                );
            }
        }
    }

    @MessageMapping("/support-chat/admin/send")
    public void adminSendMessage(@Header(name = "Authorization") String authorization, Message<ChatMessage> message) {
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            if (this.tokenProvider.validateToken(token)) {
                Long userId = tokenProvider.getUserIdFromToken(token);
                User user = this.userService.findById(userId).orElseThrow();
                this.simpMessagingTemplate.convertAndSend(
                    "/support-chat/receive/"+message.getPayload().chatWith,
                    new ChatMessage(user.getUsername(), message.getPayload().content)
                );
            }
        }
    }
}
