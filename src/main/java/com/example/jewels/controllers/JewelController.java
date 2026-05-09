package com.example.jewels.controllers;

import com.example.jewels.views.Views;
import com.example.jewels.repository.dto.Jewel;
import com.example.jewels.repository.JewelRepository;
import com.example.jewels.repository.dto.User;
import com.example.jewels.errors.NotFoundException;
import io.github.wallawood.GeminiResponse;
import io.github.wallawood.annotations.RequireAuthorized;
import io.github.wallawood.annotations.Context;
import io.github.wallawood.annotations.GeminiController;
import io.github.wallawood.annotations.Path;
import io.github.wallawood.annotations.PathParam;
import io.github.wallawood.annotations.QueryString;
import io.github.wallawood.annotations.RequireInput;

import java.io.IOException;

@GeminiController
@Path("/jewels")
@RequireAuthorized(message = "Sign up to browse jewels.")
public class JewelController {

    private final JewelRepository jewels;
    private final Views views;

    public JewelController(JewelRepository jewels, Views views) {
        this.jewels = jewels;
        this.views = views;
    }

    @Path("")
    public GeminiResponse list(@Context User user) throws IOException {
        return GeminiResponse.success(views.jewelList(jewels.findAll(), user, null));
    }

    @Path("/new")
    @RequireInput("Your shower thought:")
    public GeminiResponse create(@Context User user, @QueryString String body) {
        jewels.create(user.id(), body.trim());
        return GeminiResponse.temporaryRedirect("/jewels");
    }

    @Path("/{id}")
    public GeminiResponse view(@PathParam("id") long id, @Context User user) throws IOException {
        Jewel jewel = jewels.findById(id);
        if (jewel == null) throw new NotFoundException("Jewel not found");
        return GeminiResponse.success(views.jewelDetail(jewel, user));
    }

    @Path("/{id}/edit")
    @RequireInput("Update your jewel:")
    public GeminiResponse edit(@PathParam("id") long id, @Context User user, @QueryString String body) {
        Jewel jewel = jewels.findById(id);
        if (jewel == null) throw new NotFoundException("Jewel not found");
        if (jewel.authorId() != user.id()) {
            return GeminiResponse.certificateNotAuthorized("You can only edit your own jewels.");
        }
        jewels.update(id, body.trim());
        return GeminiResponse.temporaryRedirect("/jewels");
    }

    @Path("/{id}/delete")
    public GeminiResponse delete(@PathParam("id") long id, @Context User user) {
        Jewel jewel = jewels.findById(id);
        if (jewel == null) throw new NotFoundException("Jewel not found");
        if (jewel.authorId() != user.id() && !user.isMod()) {
            return GeminiResponse.certificateNotAuthorized("You can only delete your own jewels.");
        }
        jewels.delete(id);
        return GeminiResponse.temporaryRedirect("/jewels");
    }

    @Path("/search")
    @RequireInput("Search jewels:")
    public GeminiResponse search(@Context User user, @QueryString String query) throws IOException {
        return GeminiResponse.success(views.jewelList(jewels.search(query.trim()), user, query.trim()));
    }

    @Path("/best")
    public GeminiResponse best() {
        throw new NotFoundException("Best Jewels is not yet live.");
    }
}
