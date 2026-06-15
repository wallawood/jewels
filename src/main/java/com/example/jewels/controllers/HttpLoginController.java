package com.example.jewels.controllers;

import com.example.jewels.repository.SessionRepository;
import com.example.jewels.repository.dto.User;
import com.example.jewels.service.AuthService;
import com.example.jewels.views.Views;
import io.github.wallawood.GeminiResponse;
import io.github.wallawood.HttpResponseExtras;
import io.github.wallawood.annotations.Context;
import io.github.wallawood.annotations.GeminiController;
import io.github.wallawood.annotations.Path;
import reactor.netty.http.server.HttpServerRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@GeminiController
public class HttpLoginController {

    private final AuthService authService;
    private final SessionRepository sessions;
    private final Views views;

    public HttpLoginController(AuthService authService, SessionRepository sessions, Views views) {
        this.authService = authService;
        this.sessions = sessions;
        this.views = views;
    }

    @Path("/login")
    public GeminiResponse login(
            @Context HttpServerRequest req,
            @Context HttpResponseExtras extras,
            @Context User existingUser) throws IOException {

        if (req == null) return GeminiResponse.notFound();
        if (existingUser != null) return GeminiResponse.temporaryRedirect("/jewels");

        if ("POST".equalsIgnoreCase(req.method().name())) {
            byte[] bodyBytes = req.receive().aggregate().asByteArray().block();
            Map<String, String> form = parseForm(bodyBytes);
            String displayName = form.getOrDefault("display_name", "");
            String password = form.getOrDefault("password", "");

            User user = authService.authenticateByPassword(displayName, password);
            if (user == null) {
                return html(views.login("Invalid display name or password."));
            }

            String token = sessions.create(user.id(), Duration.ofDays(30));
            extras.secureCookie("session", token, Duration.ofDays(30));
            return GeminiResponse.temporaryRedirect("/jewels");
        }

        return html(views.login(null));
    }

    @Path("/logout")
    public GeminiResponse logout(
            @Context HttpServerRequest req,
            @Context HttpResponseExtras extras) {

        if (req == null) return GeminiResponse.notFound();

        String cookieHeader = req.requestHeaders().get("Cookie");
        if (cookieHeader != null) {
            String token = extractSessionToken(cookieHeader);
            if (token != null) sessions.delete(token);
        }

        extras.header("Set-Cookie",
                "session=; HttpOnly; Secure; Path=/; SameSite=Strict; Max-Age=0");
        return GeminiResponse.temporaryRedirect("/login");
    }

    private static GeminiResponse html(String body) {
        return GeminiResponse.success("text/html; charset=utf-8", body);
    }

    private static String extractSessionToken(String cookieHeader) {
        for (String part : cookieHeader.split(";")) {
            String trimmed = part.trim();
            if (trimmed.startsWith("session=")) {
                return trimmed.substring("session=".length());
            }
        }
        return null;
    }

    private static Map<String, String> parseForm(byte[] body) {
        Map<String, String> map = new LinkedHashMap<>();
        if (body == null || body.length == 0) return map;
        String raw = new String(body, StandardCharsets.UTF_8);
        for (String pair : raw.split("&")) {
            int eq = pair.indexOf('=');
            if (eq < 0) continue;
            try {
                String key = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8);
                String val = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
                map.put(key, val);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return map;
    }
}
