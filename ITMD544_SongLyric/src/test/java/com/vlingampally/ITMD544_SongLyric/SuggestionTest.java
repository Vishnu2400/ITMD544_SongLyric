package com.vlingampally.ITMD544_SongLyric;

import com.vlingampally.ITMD544_SongLyric.model.Song;
import com.vlingampally.ITMD544_SongLyric.model.Suggestion;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SuggestionTest {

    @Test
    public void testSuggestion() {
        Song song = new Song();
        Users suggester = new Users();
        LocalDateTime timestamp = LocalDateTime.now();

        Suggestion suggestion = new Suggestion();
        suggestion.setId(1L);
        suggestion.setSong(song);
        suggestion.setSuggester(suggester);
        suggestion.setSuggestionText("This is a suggestion.");
        suggestion.setTimestamp(timestamp);

        assertEquals(1L, suggestion.getId());
        assertEquals(song, suggestion.getSong());
        assertEquals(suggester, suggestion.getSuggester());
        assertEquals("This is a suggestion.", suggestion.getSuggestionText());
        assertEquals(timestamp, suggestion.getTimestamp());
    }


}
