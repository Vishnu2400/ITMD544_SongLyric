package com.vlingampally.ITMD544_SongLyric.security;

import jakarta.servlet.FilterChain;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class HiveApiKeyAuthenticationFilter extends OncePerRequestFilter {

    @Value("${hive.api.key}")
    private String hiveApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String hiveApiKeyHeader = request.getHeader("Authorization");
        if (hiveApiKeyHeader != null && hiveApiKeyHeader.equals("Token " + hiveApiKey)) {
            HiveApiKeyAuthenticationToken authToken = new HiveApiKeyAuthenticationToken(hiveApiKeyHeader);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}