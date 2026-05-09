package com.example.jewels.auth;

import io.github.wallawood.Authorization;
import io.github.wallawood.GeminiResponse;
import io.github.wallawood.Grant;
import io.github.wallawood.RequestContext;
import io.github.wallawood.RequestInterceptor;
import io.github.wallawood.annotations.Preprocessor;

import java.net.URI;
import java.util.Optional;

@Preprocessor(priority = 10)
public class StaticGuard implements RequestInterceptor {

    private static final Authorization MOD_REQUIRED = Authorization.requireClearance(Role.MOD);

    @Override
    public Optional<GeminiResponse> intercept(RequestContext context) {
        URI uri = context.get(URI.class);
        if (uri != null && uri.getPath().startsWith("/mod-guide")) {
            Grant grant = context.get(Grant.class);
            if (!MOD_REQUIRED.check(grant)) {
                return Optional.of(GeminiResponse.certificateNotAuthorized("Moderators only."));
            }
        }
        return Optional.empty();
    }
}
