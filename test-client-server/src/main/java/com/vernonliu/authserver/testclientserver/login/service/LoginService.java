package com.vernonliu.authserver.testclientserver.login.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Service
@Slf4j
public class LoginService {

    private static final String AUTH_CLIENT_UUID = System.getenv("AUTH_CLIENT_UUID");
    private static final String AUTH_CLIENT_SECRET = System.getenv("AUTH_CLIENT_SECRET");
    private static final String AUTH_SERVER_URL = System.getenv("AUTH_WEBAPP_ORIGIN");

    private static final URI AUTH_SERVER_SSO_ENDPOINT = URI.create(AUTH_SERVER_URL + "/api/authorization/sso");

    public String accessCodeExchange(String accessCode) {
        Map<String, String> exchangeRequest = Map.of(
                "accessCode", accessCode,
                "clientUuid", AUTH_CLIENT_UUID,
                "clientSecret", AUTH_CLIENT_SECRET
        );
        RestTemplate restRequest = new RestTemplate();
        log.info(exchangeRequest.toString());
        String result = restRequest.postForObject(AUTH_SERVER_SSO_ENDPOINT, exchangeRequest, String.class);
        log.info(result);
        return result;
    }
}
