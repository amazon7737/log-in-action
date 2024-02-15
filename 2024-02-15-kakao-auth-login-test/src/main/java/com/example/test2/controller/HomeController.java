package com.example.test2.controller;

import com.example.test2.dto.KakaoDTO;
import com.example.test2.service.KakaoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequiredArgsConstructor
@Controller
@Slf4j
public class HomeController {

    private final KakaoService kakaoService;

    @RequestMapping(value="/", method= RequestMethod.GET)
    public String login(Model model) {
        log.info("getKaKaoLogin: {}", kakaoService.getKakaoLogin());

        model.addAttribute("kakaoUrl", kakaoService.getKakaoLogin());


        return "index";
    }


}
