package com.example.sockettest2.presentation;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@ServerEndpoint("/socket/chatt")
public class WebSocketChat {
    private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());
    private static Logger logger = LoggerFactory.getLogger(WebSocketChat.class);

    @OnOpen
    public void onOpen(Session session) {
        logger.info("open session : {}, clients={}", session.toString(), clients);


        if(!clients.contains(session)) {
            clients.add(session);
            logger.info("session open : {}", session);
        }else{
            logger.info("이미 연결된 session");
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        logger.info("receive message : {}", message);

        for (Session s : clients) {
            logger.info("send data : {}", message);

            s.getBasicRemote().sendText(message);
        }
    }

    @OnClose
    public void onClose(Session session) {
        logger.info("session close : {}", session);
        clients.remove(session);
    }
}