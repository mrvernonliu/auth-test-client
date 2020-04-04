package com.vernonliu.authserver.testclientserver.login.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequestMapping("/login")
public class LoginController {

    @GetMapping("")
    public void getLoginURL(HttpServletResponse response) {
        response.setStatus(302);
        response.setHeader("Location", "http://auth.vernonliu.com/login?clientUuid=5d94fdf7-85d0-49c0-a5c2-1b87424bd716&redirectUrl=https://vernonliu.com");
    }
}
