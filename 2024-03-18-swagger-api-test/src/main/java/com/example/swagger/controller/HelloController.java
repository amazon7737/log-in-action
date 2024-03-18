package com.example.swagger.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    @ApiOperation(value = "안녕하세요", notes = "테스트로 한번 동기화를 해봤습니다.")
    public String Hello(){

        return "hello";

    }
}
