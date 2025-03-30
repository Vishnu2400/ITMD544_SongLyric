package com.vlingampally.ITMD544_SongLyric.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class HiveAIService {

    private static final String HIVE_API_URL = "https://api.thehive.ai/api/v2/task/sync";
    private static final String HIVE_API_KEY = "B5S0qMjUKFQWKgZiZvui8my0O4IqpICZ"; // Replace with your actual Hive API key
    private static final Logger logger = Logger.getLogger(HiveAIService.class.getName());

    public String getSongTitleSuggestion(String lyrics) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Token " + HIVE_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        try {
            // Construct a valid JSON structure for the request body
            ObjectMapper objectMapper = new ObjectMapper();
            String prompt = "Suggest a creative song title for these lyrics:\n\n" + lyrics;

            Map<String, Object> requestMap = Map.of(
                    "model", "llama3-8b",
                    "prompt", prompt,
                    "max_tokens", 10,
                    "temperature", 0.7
            );

            String requestBody = objectMapper.writeValueAsString(requestMap);

            logger.info("Request URL: " + HIVE_API_URL);
            logger.info("Request Headers: " + headers);
            logger.info("Request Body: " + requestBody);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(HIVE_API_URL, HttpMethod.POST, requestEntity, String.class);

            logger.info("Response Status Code: " + response.getStatusCode());
            logger.info("Response Body: " + response.getBody());

            return response.getBody();
        } catch (HttpClientErrorException e) {
            logger.severe("Error Status Code: " + e.getStatusCode());
            logger.severe("Error Response Body: " + e.getResponseBodyAsString());
            return "Error: " + e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.severe("Unexpected Error: " + e.getMessage());
            return "Unexpected Error: " + e.getMessage();
        }
    }
}
