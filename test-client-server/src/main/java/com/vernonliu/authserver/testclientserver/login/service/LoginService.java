package com.vernonliu.authserver.testclientserver.login.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import java.net.URI;
import java.util.Map;

@Service
@Slf4j
public class LoginService {

    private static final String AUTH_CLIENT_UUID = System.getenv("AUTH_CLIENT_UUID");
    private static final String AUTH_CLIENT_SECRET = System.getenv("AUTH_CLIENT_SECRET");
    private static final String AUTH_SERVER_URL = System.getenv("AUTH_WEBAPP_ORIGIN");
    private static final String COOKIE_DOMAIN = System.getenv("COOKIE_DOMAIN");

    private static final URI AUTH_SERVER_SSO_ENDPOINT = URI.create(AUTH_SERVER_URL + "/api/authorization/sso");

    public Cookie[] accessCodeExchange(String accessCode) {
        Map<String, String> exchangeRequest = Map.of(
                "accessCode", accessCode,
                "clientUuid", AUTH_CLIENT_UUID,
                "clientSecret", AUTH_CLIENT_SECRET
        );
        RestTemplate restRequest = new RestTemplate();
        log.info(exchangeRequest.toString());
        Map result = restRequest.postForObject(AUTH_SERVER_SSO_ENDPOINT, exchangeRequest, Map.class);
        if (result == null) return null;
        log.info(result.toString());
        Cookie[] cookies = new Cookie[2];
        String identityToken = (String) result.getOrDefault("ti", "ERROR");
        String accessToken = (String) result.getOrDefault("ta", "ERROR");

        cookies[0] = new Cookie("ti", identityToken);
        cookies[1] = new Cookie("ta", accessToken);
        for (Cookie c: cookies) {
            c.setDomain(COOKIE_DOMAIN);
            c.setPath("/");
        }
        return cookies;
    }
}
