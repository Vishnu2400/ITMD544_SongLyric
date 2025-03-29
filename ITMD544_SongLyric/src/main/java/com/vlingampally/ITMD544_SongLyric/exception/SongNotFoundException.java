package com.vlingampally.ITMD544_SongLyric.exception;

public class SongNotFoundException extends RuntimeException {
    public SongNotFoundException(Long id) {
        super("Song not found with id: " + id);
    }
}
