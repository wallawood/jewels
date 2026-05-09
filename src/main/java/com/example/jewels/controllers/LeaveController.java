package com.example.jewels.controllers;

import com.example.jewels.views.Views;
import com.example.jewels.repository.JewelRepository;
import com.example.jewels.repository.dto.User;
import com.example.jewels.repository.UserRepository;
import io.gemboot.GeminiResponse;
import io.gemboot.annotations.RequireAuthorized;
import io.gemboot.annotations.Context;
import io.gemboot.annotations.GeminiController;
import io.gemboot.annotations.Path;
import io.gemboot.annotations.QueryString;
import io.gemboot.annotations.RequireSensitiveInput;

import java.io.IOException;

@GeminiController
public class LeaveController {

    private final UserRepository users;
    private final JewelRepository jewels;
    private final Views views;

    public LeaveController(UserRepository users, JewelRepository jewels, Views views) {
        this.users = users;
        this.jewels = jewels;
        this.views = views;
    }

    @Path("/leave")
    @RequireAuthorized(message = "You need to be signed in to leave.")
    @RequireSensitiveInput("Type DELETE to confirm:")
    public GeminiResponse leave(@Context User user, @QueryString String confirmation) throws IOException {
        if (!"DELETE".equals(confirmation.trim())) {
            return GeminiResponse.success(views.leaveCancelled(confirmation.trim()));
        }
        jewels.deleteByAuthor(user.id());
        users.delete(user.id());
        return GeminiResponse.success(views.leaveGoodbye());
    }
}
