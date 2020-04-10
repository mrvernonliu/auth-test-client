package com.vernonliu.authserver.testclientserver.authentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
    private static final URI AUTH_SERVER_LOGOUT_ENDPOINT = URI.create(AUTH_SERVER_URL + "/api/authorization/logout");
    private static final URI AUTH_SERVER_VALIDATION_ENDPOINT = URI.create(AUTH_SERVER_URL + "/api/authorization/reference-token-validation");

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

    //BTW I don't actually check for authorization on self-contained tokens, just
    // if they are valid jwt's or not.
    public boolean isValidateAccessToken(HttpServletRequest request) {
        if (request == null || request.getCookies() == null) return false;
        Cookie accessToken = Arrays.stream(request.getCookies())
                .filter((cookie) -> "ta".equals(cookie.getName()))
                .findFirst()
                .orElse(null);
        if (accessToken == null) return false;
        log.info(accessToken.getValue());

        if (parseJwt(accessToken) == null) return false;

        Date expiration = Jwts.parserBuilder()
                .setSigningKey(publicKey).build()
                .parseClaimsJws(accessToken.getValue())
                .getBody()
                .getExpiration();
        Date now = new Date();
        if (expiration.before(now)) return false;
        Claims claims = parseJwt(accessToken);
        if (claims == null) return false;
        String referenceToken = (String)claims.get("referenceToken");
        String accountUuid = (String)claims.get("sub");

        if(!StringUtils.isEmpty(referenceToken)) {
            return validateReferenceToken(referenceToken, accountUuid);
        }
        return true;
    }

    private boolean validateReferenceToken(String referenceToken, String accountUuid) {
        Map<String, String> requestBody = Map.of(
                "accountUuid", accountUuid,
                "referenceToken", referenceToken,
                "clientUuid", AUTH_CLIENT_UUID,
                "clientSecret", AUTH_CLIENT_SECRET
        );
        RestTemplate restRequest = new RestTemplate();
        Map result;
        try {
            result = restRequest.postForObject(AUTH_SERVER_VALIDATION_ENDPOINT, requestBody, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        log.info("Validation request successful");
        return true;
    }

    public boolean logoutFromIDP(HttpServletRequest request) {
        Cookie ta = getCookie(request, "ta");
        if (ta == null) return false;
        Claims claims = parseJwt(ta);
        if (claims == null) return false;
        String accountId = (String)claims.get("sub");
        Map<String, String> logoutRequest = Map.of(
                "accountUuid", accountId,
                "clientUuid", AUTH_CLIENT_UUID,
                "clientSecret", AUTH_CLIENT_SECRET
        );
        log.info(logoutRequest.toString());
        return dispatchLogoutRequestToIdp(logoutRequest);
    }

    private boolean dispatchLogoutRequestToIdp(Map<String, String> logoutRequest) {
        RestTemplate restRequest = new RestTemplate();
        log.info(logoutRequest.toString());
        Map result;
        try {
            result = restRequest.postForObject(AUTH_SERVER_LOGOUT_ENDPOINT, logoutRequest, Map.class);
        } catch (Exception e) {
            log.error("Failed to logout: {}", e.getMessage());
        }
        log.info("Logout successful");
        return true;
    }

    private Cookie getCookie(HttpServletRequest request, String cookieName) {
        if (request == null || request.getCookies() == null) {
            log.warn("Cannot refresh - no cookies attached");
            return null;
        }
        Cookie cookie = Arrays.stream(request.getCookies())
                .filter((c) -> cookieName.equals(c.getName()))
                .findFirst()
                .orElse(null);
        return cookie;
    }

    private Claims parseJwt(Cookie accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(accessToken.getValue().strip()).getBody();
        } catch (JwtException e) {
            log.error("Failed to validate JWT: {}", e.getMessage());
            return null;
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
