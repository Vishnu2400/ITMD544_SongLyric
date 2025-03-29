package com.vlingampally.ITMD544_SongLyric.exception;

public class SuggestionNotFoundException extends RuntimeException {
    public SuggestionNotFoundException(Long id) {
        super("Suggestion not found with id: " + id);
    }
}
