package com.example.sockettest2.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.logging.Handler;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        registry.addHandler(signalingSocketHandler(), "/room") // 웹 소켓 서버 end point
                .setAllowedOrigins("*"); // CORS
    }

    public WebSocketHandler signalingSocketHandler(){
        return new com.example.sockettest2.infrastructure.config.WebSocketHandler();
    }
}
