package org.example.applicationtest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    Environment env;

    @GetMapping("/status")
    public String status(){
        return "Status - returned by Pod - " + env.getProperty("HOSTNAME");
    }

}
