package com.example.sockettest2.infrastructure.config;

import com.example.sockettest2.infrastructure.stomp.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        String sessionId = session.getId();
        sessions.put(sessionId, session); // 세션 저장

        Message message = Message.builder()
                .sender(sessionId)
                .receiver("all")
                .build();

        message.newConnect();

        sessions.values().forEach(s -> {
            try{
                if(!s.getId().equals(sessionId)){
                    s.sendMessage(new TextMessage("hello this is" + sessionId));
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        });
        super.afterConnectionEstablished(session);
    }

    @Override // 양방향 통신 로직
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception{
        Message message = mapper.readValue(textMessage.getPayload(), Message.class);
        message.setSender(session.getId());

        log.info("### {}", message.getData());
        log.info("### {}", message.getReceiver());
        WebSocketSession receiver = sessions.get(message.getReceiver()); // 메시지 수신자를 찾는다.

        if(receiver != null && receiver.isOpen()){ // 수신자가 존재하고 연결된 상태일 경우 메시지를 전송한다.
            receiver.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        }
    }

    /**
     * 소켓 통신 에러
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Message  message = new Message("error", "server" , session.getId(), exception.getMessage());
        session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));

        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
        String sessionId = session.getId();
        sessions.remove(sessionId);

        final Message message = new Message();
        message.closeConnect();
        message.setSender(sessionId);

        sessions.values().forEach(s -> {
            try{
                s.sendMessage(new TextMessage(mapper.writeValueAsString(message)));; //  다른 사용자에게 접속종료를 알린다.
            }catch (IOException e){
                log.info("### ex ", e);
            }
        });
    }
}
