package com.vlingampally.ITMD544_SongLyric;

import com.vlingampally.ITMD544_SongLyric.model.Song;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SongTest {

    @Test
    public void testSong() {
        Users author = new Users();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Song song = new Song();
        song.setId(1L);
        song.setTitle("Song Title");
        song.setLyrics("These are the lyrics.");
        song.setAuthor(author);
        song.setCreatedAt(createdAt);
        song.setUpdatedAt(updatedAt);

        assertEquals(1L, song.getId());
        assertEquals("Song Title", song.getTitle());
        assertEquals("These are the lyrics.", song.getLyrics());
        assertEquals(author, song.getAuthor());
        assertEquals(createdAt, song.getCreatedAt());
        assertEquals(updatedAt, song.getUpdatedAt());
    }
}
