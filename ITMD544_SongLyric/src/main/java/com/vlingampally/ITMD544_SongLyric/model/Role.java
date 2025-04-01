package com.vlingampally.ITMD544_SongLyric.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    SONG_WRITER,
    CONTRIBUTOR;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
