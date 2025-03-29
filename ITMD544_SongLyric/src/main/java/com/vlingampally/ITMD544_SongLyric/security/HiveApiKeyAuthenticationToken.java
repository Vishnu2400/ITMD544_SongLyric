package com.vlingampally.ITMD544_SongLyric.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class HiveApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final String hiveApiKey;

    public HiveApiKeyAuthenticationToken(String hiveApiKey) {
        super(null);
        this.hiveApiKey = hiveApiKey;
        setAuthenticated(true); // Indicate that the token is already authenticated
    }

    @Override
    public Object getCredentials() {
        return hiveApiKey;
    }

    @Override
    public Object getPrincipal() {
        return hiveApiKey;
    }
}