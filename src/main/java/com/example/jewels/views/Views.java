package com.example.jewels.views;

import com.example.jewels.repository.dto.Jewel;
import com.example.jewels.repository.dto.User;
import com.github.jknack.handlebars.Handlebars;
import io.github.wallawood.annotations.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Views {

    private static final String PICO_CSS = loadPicoCss();

    private static String loadPicoCss() {
        try (InputStream in = Views.class.getResourceAsStream("/pico.classless.min.css")) {
            return in != null ? new String(in.readAllBytes(), StandardCharsets.UTF_8) : "";
        } catch (IOException e) {
            return "";
        }
    }

    private final Handlebars hbs = new Handlebars();

    public String jewelList(List<Jewel> jewels, User user, String query, boolean isHttp) throws IOException {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("jewels", jewels.stream().map(j -> jewelMap(j, user)).toList());
        ctx.put("user", userMap(user));
        ctx.put("query", query != null ? query : "");
        ctx.put("isHttp", isHttp);
        ctx.put("isLoggedIn", user != null);
        return hbs.compile("templates/list").apply(ctx);
    }

    public String jewelDetail(Jewel jewel, User user) throws IOException {
        return hbs.compile("templates/jewel").apply(Map.of("jewel", jewelMap(jewel, user)));
    }

    public String signupExists(String displayName) throws IOException {
        return hbs.compile("templates/signup-exists").apply(Map.of("displayName", displayName));
    }

    public String login(String error) throws IOException {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("css", PICO_CSS);
        ctx.put("error", error != null ? error : "");
        return hbs.compile("templates/login").apply(ctx);
    }

    public String httpSignup(String error) throws IOException {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("css", PICO_CSS);
        ctx.put("error", error != null ? error : "");
        return hbs.compile("templates/http-signup").apply(ctx);
    }

    public String leaveCancelled(String input) throws IOException {
        return hbs.compile("templates/leave-cancelled").apply(Map.of("input", input));
    }

    private Map<String, Object> jewelMap(Jewel j, User user) {
        boolean isOwner = j.authorId() == user.id();
        return Map.of(
                "id", j.id(),
                "authorName", j.authorName(),
                "body", j.body(),
                "canEdit", isOwner,
                "canDelete", isOwner || user.isMod());
    }

    private Map<String, Object> userMap(User u) {
        return Map.of("displayName", u.displayName(), "isMod", u.isMod());
    }
}
