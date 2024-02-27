package com.example.sockettest2.presentation;

import com.example.sockettest2.infrastructure.stomp.StompMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    public void message(StompMessage message){
        simpMessageSendingOperations.convertAndSend("/sub/channel"+message.getChannelId(), message);
    }
}
