package com.vlingampally.ITMD544_SongLyric.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class HiveApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String HIVE_API_KEY = "B5S0qMjUKFQWKgZiZvui8my0O4IqpICZ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String hiveApiKeyHeader = request.getHeader("Hive-Api-Key");
        if (hiveApiKeyHeader != null && hiveApiKeyHeader.equals(HIVE_API_KEY)) {
            // If the Hive API key is valid, set an authentication token
            HiveApiKeyAuthenticationToken authToken = new HiveApiKeyAuthenticationToken(hiveApiKeyHeader);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}