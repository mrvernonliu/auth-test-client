package com.vernonliu.authserver.testclientserver.login.controller;

import com.vernonliu.authserver.testclientserver.login.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequestMapping("/login")
public class LoginController {

    private static final String SELF_DOMAIN_URL = System.getenv("SELF_DOMAIN_URL");

    @Autowired
    LoginService loginService;

    @PostMapping("/sso")
    public ResponseEntity<?> getSSOTokens(HttpServletResponse response, @RequestParam String accessCode) {
        String tokens = loginService.accessCodeExchange(accessCode);
        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }
}
