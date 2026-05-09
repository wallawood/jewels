package com.example.jewels.controllers;

import com.example.jewels.service.AuthService;
import com.example.jewels.auth.Role;
import com.example.jewels.views.Views;
import io.gemboot.CertUtil;
import io.gemboot.GeminiResponse;
import io.gemboot.annotations.Context;
import io.gemboot.annotations.GeminiController;
import io.gemboot.annotations.Path;
import io.gemboot.annotations.QueryString;
import io.gemboot.annotations.RequireCertificate;
import io.gemboot.annotations.RequireInput;

import java.io.IOException;
import java.security.cert.X509Certificate;

@GeminiController
@Path("/signup")
@RequireCertificate
public class SignupController {

    private final AuthService authService;
    private final Views views;

    public SignupController(AuthService authService, Views views) {
        this.authService = authService;
        this.views = views;
    }

    @Path("")
    public GeminiResponse choose(@Context X509Certificate cert) throws IOException {
        if (authService.isRegistered(cert)) {
            return GeminiResponse.success(views.signupExists(CertUtil.cn(cert)));
        }
        return GeminiResponse.success(views.signupChoose());
    }

    @Path("/user")
    @RequireInput("Choose a display name:")
    public GeminiResponse signupUser(@Context X509Certificate cert, @QueryString String name) {
        return createUser(cert, name, Role.USER);
    }

    @Path("/mod")
    @RequireInput("Choose a display name:")
    public GeminiResponse signupMod(@Context X509Certificate cert, @QueryString String name) {
        return createUser(cert, name, Role.MOD);
    }

    private GeminiResponse createUser(X509Certificate cert, String name, int level) {
        if (authService.isRegistered(cert)) {
            return GeminiResponse.temporaryRedirect("/signup");
        }
        authService.register(cert, name.trim(), level);
        return GeminiResponse.temporaryRedirect("/jewels");
    }
}
