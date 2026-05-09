package com.example.jewels.auth;

import com.example.jewels.repository.dto.User;
import com.example.jewels.service.AuthService;
import io.gemboot.GeminiResponse;
import io.gemboot.Grant;
import io.gemboot.RequestContext;
import io.gemboot.RequestInterceptor;
import io.gemboot.annotations.Preprocessor;

import java.security.cert.X509Certificate;
import java.util.Optional;

@Preprocessor(priority = 0)
public class Authority implements RequestInterceptor {

    private final AuthService authService;

    public Authority(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Optional<GeminiResponse> intercept(RequestContext context) {
        X509Certificate cert = context.get(X509Certificate.class);
        if (cert == null) return Optional.empty();

        User user = authService.authenticate(cert);
        if (user == null) return Optional.empty();

        context.add(user);
        context.add(Grant.clearance(user.level()));
        return Optional.empty();
    }
}
