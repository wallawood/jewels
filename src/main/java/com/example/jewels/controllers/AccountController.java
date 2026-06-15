package com.example.jewels.controllers;

import com.example.jewels.auth.Role;
import com.example.jewels.repository.dto.User;
import com.example.jewels.service.AuthService;
import io.github.wallawood.GeminiResponse;
import io.github.wallawood.annotations.Context;
import io.github.wallawood.annotations.GeminiController;
import io.github.wallawood.annotations.Path;
import io.github.wallawood.annotations.QueryString;
import io.github.wallawood.annotations.RequireCertificate;
import io.github.wallawood.annotations.RequireClearance;
import io.github.wallawood.annotations.RequireSensitiveInput;

@GeminiController
@Path("/account")
@RequireCertificate("A client certificate is required to manage your account.")
@RequireClearance(level = Role.USER, message = "Sign up at /signup to access your account.")
public class AccountController {

    private final AuthService authService;

    public AccountController(AuthService authService) {
        this.authService = authService;
    }

    @Path("")
    public GeminiResponse index(@Context User user) {
        String body = "# Account\n\n"
                + "Signed in as: " + user.displayName() + "\n\n"
                + "=> /account/set-password Set web login password\n"
                + "=> /jewels Browse jewels\n";
        return GeminiResponse.success(body);
    }

    @Path("/set-password")
    @RequireSensitiveInput("Enter a new password for web login:")
    public GeminiResponse setPassword(@Context User user, @QueryString String password) {
        authService.setPassword(user.id(), password);
        String body = "# Password Set\n\n"
                + "Your web login password has been configured.\n\n"
                + "=> /login Log in via the web\n"
                + "=> /account Back to account\n";
        return GeminiResponse.success(body);
    }
}
