package com.foober.foober.service;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ScheduledPushMessages {

    private final SimpMessagingTemplate simpMessagingTemplate;


}
