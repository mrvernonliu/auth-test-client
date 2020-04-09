package com.vernonliu.authserver.testclientserver.authentication.controller;

import com.vernonliu.authserver.testclientserver.authentication.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@Slf4j
public class LogoutController {
    @Autowired
    AuthenticationService authenticationService;

    // TODO: call auth server and invalidate the user's refresh token.
    @PostMapping("/sso/invalidate")
    @ResponseBody
    public Map logout(HttpServletResponse response) {
        log.info("A user is logging out"); // TODO: make more specific later when incorporating cookies
        Map cookieClearingDetails = authenticationService.getLogoutDetails();
        if (cookieClearingDetails == null) {
            response.setStatus(500);
            return null;
        }
        response.setStatus(200);
        return cookieClearingDetails;
    }
}
