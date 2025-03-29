package com.vlingampally.ITMD544_SongLyric.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Suggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users Suggester;
    @Lob
    private String suggestionText;
    private LocalDateTime timestamp;

    public Song getSong() {
        return song;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public void setSuggester(Users suggester) {
        Suggester = suggester;
    }

    public void setSuggestionText(String suggestionText) {
        this.suggestionText = suggestionText;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public Users getSuggester() {
        return Suggester;
    }

    public String getSuggestionText() {
        return suggestionText;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}