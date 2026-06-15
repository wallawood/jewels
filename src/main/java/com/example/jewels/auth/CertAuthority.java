package com.example.jewels.auth;

import com.example.jewels.repository.dto.User;
import com.example.jewels.service.AuthService;
import io.github.wallawood.GeminiResponse;
import io.github.wallawood.Grant;
import io.github.wallawood.RequestContext;
import io.github.wallawood.RequestInterceptor;
import io.github.wallawood.annotations.Preprocessor;

import java.security.cert.X509Certificate;
import java.util.Optional;

@Preprocessor(priority = 1)
public class CertAuthority implements RequestInterceptor {

    private final AuthService authService;

    public CertAuthority(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Optional<GeminiResponse> intercept(RequestContext context) {
        X509Certificate cert = context.get(X509Certificate.class);
        if (cert == null)
            return Optional.empty();

        User user = authService.authenticate(cert);
        if (user == null)
            return Optional.empty();

        context.add(user);
        context.add(Grant.clearance(user.level()));
        return Optional.empty();
    }
}
