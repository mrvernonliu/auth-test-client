package com.vernonliu.authserver.testclientserver.authentication.controller;

import com.vernonliu.authserver.testclientserver.authentication.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequestMapping("/login")
public class AuthenticationController {

    private static final String SELF_DOMAIN_URL = System.getenv("SELF_DOMAIN_URL");

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/sso")
    public void getSSOTokens(HttpServletResponse response, @RequestParam String accessCode) {
        Cookie[] tokens = authenticationService.accessCodeExchange(accessCode);
        response.addCookie(tokens[0]);
        response.addCookie(tokens[1]);
        response.setStatus(204);
        response.setHeader("Location", SELF_DOMAIN_URL);
    }
}
