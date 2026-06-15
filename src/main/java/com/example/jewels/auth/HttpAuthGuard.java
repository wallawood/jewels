package com.example.jewels.auth;

import com.example.jewels.repository.dto.User;
import io.github.wallawood.GeminiResponse;
import io.github.wallawood.RequestContext;
import io.github.wallawood.RequestInterceptor;
import io.github.wallawood.annotations.Preprocessor;
import reactor.netty.http.server.HttpServerRequest;

import java.util.Optional;

@Preprocessor(priority = 20)
public class HttpAuthGuard implements RequestInterceptor {

    @Override
    public Optional<GeminiResponse> intercept(RequestContext context) {
        HttpServerRequest req = context.get(HttpServerRequest.class);
        if (req == null) return Optional.empty();

        String path = req.path();
        User user = context.get(User.class);

        // /signup is Gemini-only (cert-based). HTTP visitors never reach it.
        if (path.startsWith("/signup")) {
            return Optional.of(GeminiResponse.temporaryRedirect(
                    user != null ? "/jewels" : "/http-signup"));
        }

        if (user != null) return Optional.empty();

        if (requiresAuth(path)) {
            return Optional.of(GeminiResponse.temporaryRedirect("/login"));
        }

        return Optional.empty();
    }

    private static boolean requiresAuth(String path) {
        return path.startsWith("/jewels")
                || path.startsWith("/leave")
                || path.startsWith("/account");
    }
}
