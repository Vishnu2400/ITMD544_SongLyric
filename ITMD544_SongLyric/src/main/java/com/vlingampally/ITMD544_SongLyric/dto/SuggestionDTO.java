package com.vlingampally.ITMD544_SongLyric.dto;

import java.time.LocalDateTime;

public class SuggestionDTO {
    private Long id;
    private String songTitle;
    private String suggesterUsername;
    private String suggestionText;
    private LocalDateTime timestamp;

    // Constructors, getters, and setters

    public SuggestionDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSuggesterUsername() {
        return suggesterUsername;
    }

    public void setSuggesterUsername(String suggesterUsername) {
        this.suggesterUsername = suggesterUsername;
    }

    public String getSuggestionText() {
        return suggestionText;
    }

    public void setSuggestionText(String suggestionText) {
        this.suggestionText = suggestionText;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}