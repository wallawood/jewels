package com.example.jewels.repository.dto;

import com.example.jewels.auth.Role;

public record User(long id, String certHash, String displayName, int level) {
    public boolean isMod() { return level >= Role.MOD; }
}
