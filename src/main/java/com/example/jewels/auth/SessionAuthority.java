package com.example.jewels.auth;

import com.example.jewels.repository.SessionRepository;
import com.example.jewels.repository.UserRepository;
import com.example.jewels.repository.dto.Session;
import com.example.jewels.repository.dto.User;
import io.github.wallawood.GeminiResponse;
import io.github.wallawood.Grant;
import io.github.wallawood.RequestContext;
import io.github.wallawood.RequestInterceptor;
import io.github.wallawood.annotations.Preprocessor;
import reactor.netty.http.server.HttpServerRequest;

import java.util.Optional;

@Preprocessor(priority = 0)
public class SessionAuthority implements RequestInterceptor {

    private final SessionRepository sessions;
    private final UserRepository users;

    public SessionAuthority(SessionRepository sessions, UserRepository users) {
        this.sessions = sessions;
        this.users = users;
    }

    @Override
    public Optional<GeminiResponse> intercept(RequestContext context) {
        HttpServerRequest req = context.get(HttpServerRequest.class);
        if (req == null)
            return Optional.empty();

        String cookieHeader = req.requestHeaders().get("Cookie");
        if (cookieHeader == null)
            return Optional.empty();

        String token = extractSessionToken(cookieHeader);
        if (token == null)
            return Optional.empty();

        Session session = sessions.findByToken(token);
        if (session == null || session.isExpired())
            return Optional.empty();

        User user = users.findById(session.userId());
        if (user == null)
            return Optional.empty();

        context.add(user);
        context.add(Grant.clearance(user.level()));
        return Optional.empty();
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
}
