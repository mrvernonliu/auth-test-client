package com.vernonliu.authserver.testclientserver.testdata.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/testdata")
@Slf4j
@CrossOrigin
public class TestDataController {

    @ResponseBody
    @GetMapping("")
    public ResponseEntity<?> getProtectedData(HttpServletRequest request) {
        log.info("Trying to access protected data");
        boolean loggedIn = false;
        if (loggedIn) {
            return new ResponseEntity<>("Here is some protected data!", HttpStatus.OK);
        }
        return new ResponseEntity<>("Please log in to review protected data", HttpStatus.FORBIDDEN);
    }
}
