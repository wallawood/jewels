package com.example.jewels.controllers;

import com.example.jewels.auth.Role;
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
public class HttpSignupController {

    private final AuthService authService;
    private final SessionRepository sessions;
    private final Views views;

    public HttpSignupController(AuthService authService, SessionRepository sessions, Views views) {
        this.authService = authService;
        this.sessions = sessions;
        this.views = views;
    }

    @Path("/http-signup")
    public GeminiResponse signup(
            @Context HttpServerRequest req,
            @Context HttpResponseExtras extras,
            @Context User existingUser) throws IOException {

        if (req == null) return GeminiResponse.notFound();
        if (existingUser != null) return GeminiResponse.temporaryRedirect("/jewels");

        if ("POST".equalsIgnoreCase(req.method().name())) {
            byte[] bodyBytes = req.receive().aggregate().asByteArray().block();
            Map<String, String> form = parseForm(bodyBytes);
            String displayName = form.getOrDefault("display_name", "").trim();
            String password = form.getOrDefault("password", "");
            String confirm = form.getOrDefault("confirm_password", "");

            if (displayName.isEmpty()) {
                return html(views.httpSignup("Display name is required."));
            }
            if (password.length() < 8) {
                return html(views.httpSignup("Password must be at least 8 characters."));
            }
            if (!password.equals(confirm)) {
                return html(views.httpSignup("Passwords do not match."));
            }

            try {
                User user = authService.registerByPassword(displayName, Role.USER, password);
                String token = sessions.create(user.id(), Duration.ofDays(30));
                extras.secureCookie("session", token, Duration.ofDays(30));
                return GeminiResponse.temporaryRedirect("/jewels");
            } catch (Exception e) {
                return html(views.httpSignup("That display name is already taken."));
            }
        }

        return html(views.httpSignup(null));
    }

    private static GeminiResponse html(String body) {
        return GeminiResponse.success("text/html; charset=utf-8", body);
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
