package com.vernonliu.authserver.testclientserver.testdata.controller;

import com.vernonliu.authserver.testclientserver.authentication.service.AuthenticationService;
import com.vernonliu.authserver.testclientserver.testdata.entity.Navbar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Map;

@Controller
@RequestMapping("/testdata")
@Slf4j
@CrossOrigin
public class TestDataController {

    private static final Navbar navbar = new Navbar();

    @Autowired
    AuthenticationService authenticationService;

    @ResponseBody
    @GetMapping("")
    public ResponseEntity<?> getProtectedData(HttpServletRequest request) {
        log.info("Trying to access protected data");

        if (authenticationService.isValidateAccessToken(request)) {
            return new ResponseEntity<>("Here is some protected data!", HttpStatus.OK);
        }
        return new ResponseEntity<>("Please log in to review protected data", HttpStatus.FORBIDDEN);
    }

    @ResponseBody
    @GetMapping("/navbar")
    public Navbar getNavbarInformation() {
        log.info(navbar.APPLICATION_TITLE);
        log.info(navbar.LOGIN_URL);

        return navbar;
    }
}
