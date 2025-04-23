package com.vlingampally.ITMD544_SongLyric.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class HiveAIService {

    private static final String HIVE_API_URL = "https://api.thehive.ai/api/v3/chat/completions";
    @Value("${hive.api.key}")
    private String hiveApiKey;

    public List<String> getSongTitleSuggestions(String lyrics) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + hiveApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        try {
            // Adjust the prompt to request multiple title suggestions
            ObjectMapper objectMapper = new ObjectMapper();
            String prompt = "Suggest one creative and original song title for the following lyrics. " +
                    "Respond with a comma-separated list of titles:\n\n" + lyrics;

            Map<String, Object> requestMap = Map.of(
                    "model", "meta-llama/llama-3.2-1b-instruct",
                    "max_tokens", 100,
                    "messages", new Object[]{
                            Map.of("role", "user", "content", prompt)
                    }
            );

            String requestBody = objectMapper.writeValueAsString(requestMap);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(HIVE_API_URL, HttpMethod.POST, requestEntity, String.class);

            // Extract and return multiple titles from the response
            String responseBody = response.getBody();
            if (responseBody != null) {
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode contentNode = rootNode.path("choices").path(0).path("message").path("content");
                if (!contentNode.isMissingNode()) {
                    String suggestions = contentNode.asText();
                    // Assuming the response is a comma-separated list of titles
                    return List.of(suggestions.split(",\\s*"));
                }
            }
            return List.of("No title suggestions found.");
        } catch (HttpClientErrorException e) {
            return List.of("Error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return List.of("Unexpected Error: " + e.getMessage());
        }
    }
}