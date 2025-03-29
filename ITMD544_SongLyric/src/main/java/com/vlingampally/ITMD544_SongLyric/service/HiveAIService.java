package com.vlingampally.ITMD544_SongLyric.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

@Service
public class HiveAIService {

    private static final String HIVE_API_URL = "https://api.thehive.ai/api/v2/task/sync";
    private static final String HIVE_API_KEY = "B5S0qMjUKFQWKgZiZvui8my0O4IqpICZ";

    public String getSongTitleSuggestion(String lyrics) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + HIVE_API_KEY);
        headers.set("accept", "application/json");
        headers.set("Content-Type", "application/json");

        String requestBody = "{ \"model\": \"llama3-8b\", \"prompt\": \"Suggest a creative song title for lyrics: " + lyrics + "\" }";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(HIVE_API_URL, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }
}