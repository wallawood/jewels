package com.example.jewels.views;

import com.example.jewels.repository.dto.Jewel;
import com.example.jewels.repository.dto.User;
import com.github.jknack.handlebars.Handlebars;
import io.github.wallawood.annotations.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class Views {

    private final Handlebars hbs = new Handlebars();

    public String jewelList(List<Jewel> jewels, User user, String query) throws IOException {
        return hbs.compile("templates/list").apply(Map.of(
                "jewels", jewels.stream().map(j -> jewelMap(j, user)).toList(),
                "user", userMap(user),
                "query", query != null ? query : ""));
    }

    public String jewelDetail(Jewel jewel, User user) throws IOException {
        return hbs.compile("templates/jewel").apply(Map.of("jewel", jewelMap(jewel, user)));
    }

    public String signupChoose() throws IOException {
        return hbs.compile("templates/signup-choose").apply(null);
    }

    public String signupExists(String displayName) throws IOException {
        return hbs.compile("templates/signup-exists").apply(Map.of("displayName", displayName));
    }

    public String leaveCancelled(String input) throws IOException {
        return hbs.compile("templates/leave-cancelled").apply(Map.of("input", input));
    }

    public String leaveGoodbye() throws IOException {
        return hbs.compile("templates/leave-goodbye").apply(null);
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
