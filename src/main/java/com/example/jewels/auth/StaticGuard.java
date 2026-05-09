package com.example.jewels.auth;

import io.gemboot.Authorization;
import io.gemboot.GeminiResponse;
import io.gemboot.Grant;
import io.gemboot.RequestContext;
import io.gemboot.RequestInterceptor;
import io.gemboot.annotations.Preprocessor;

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
