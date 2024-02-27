package com.example.sockettest2.infrastructure.stomp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String type;
    private String sender;
    private String receiver;
    private Object data;

    public void setSender(String sender){
        this.sender = sender;
    }

    public void newConnect(){
        this.type = "new";
    }

    public void closeConnect(){
        this.type = "close";
    }

}
