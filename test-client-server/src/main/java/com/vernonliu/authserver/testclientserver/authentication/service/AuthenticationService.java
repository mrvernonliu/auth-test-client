package com.vernonliu.authserver.testclientserver.authentication.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class AuthenticationService {

    private static final String AUTH_CLIENT_UUID = System.getenv("AUTH_CLIENT_UUID");
    private static final String AUTH_CLIENT_SECRET = System.getenv("AUTH_CLIENT_SECRET");
    private static final String AUTH_SERVER_URL = System.getenv("AUTH_WEBAPP_ORIGIN");
    private static final String COOKIE_DOMAIN = System.getenv("COOKIE_DOMAIN");
    private static final String COOKIE_PATH = "/";
    private static Key publicKey;

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
            c.setPath(COOKIE_PATH);
        }
        return cookies;
    }

    public boolean isValidateAccessToken(HttpServletRequest request) {
        if (request == null || request.getCookies() == null) return false;
        Cookie accessToken = Arrays.stream(request.getCookies())
                .filter((cookie) -> "ta".equals(cookie.getName()))
                .findFirst()
                .orElse(null);
        if (accessToken == null) return false;
        log.info(accessToken.getValue());

        if (!parseJwt(accessToken)) return false;

        Date expiration = Jwts.parserBuilder()
                .setSigningKey(publicKey).build()
                .parseClaimsJws(accessToken.getValue())
                .getBody()
                .getExpiration();
        Date now = new Date();
        return !expiration.before(now);
    }

    private boolean parseJwt(Cookie accessToken) {
        try {
            Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(accessToken.getValue().strip());
            return true;
        } catch (JwtException e) {
            log.error("Failed to validate JWT: {}", e.getMessage());
            return false;
        }
    }

    public Map<String, String> getLogoutDetails() {
        return Map.of("DOMAIN", COOKIE_DOMAIN, "PATH", COOKIE_PATH);
    }

    private static Key decodePublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String rawPublicKey = System.getenv(("AUTH_JWT_PUBLIC_KEY"));
        rawPublicKey = rawPublicKey.replace("-----BEGIN PUBLIC KEY-----", "");
        rawPublicKey = rawPublicKey.replace("-----END PUBLIC KEY-----", "");
        rawPublicKey = rawPublicKey.replace("\n", "");
        log.info(rawPublicKey);

        byte[] key = Base64.getDecoder().decode(rawPublicKey);

        // create a key object from the bytes
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(key);
        return keyFactory.generatePublic(encodedKeySpec);
    }

    public AuthenticationService () throws InvalidKeySpecException, NoSuchAlgorithmException {
        publicKey = AuthenticationService.decodePublicKey();
    }
}
