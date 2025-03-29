package com.vlingampally.ITMD544_SongLyric;

import com.vlingampally.ITMD544_SongLyric.model.Comment;
import com.vlingampally.ITMD544_SongLyric.model.Song;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTest {
    @Test
    public void testComment() {
        Song song = new Song();
        Users commenter = new Users();
        LocalDateTime timestamp = LocalDateTime.now();

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setSong(song);
        comment.setCommenter(commenter);
        comment.setCommentText("This is a comment.");
        comment.setTimestamp(timestamp);

        assertEquals(1L, comment.getId());
        assertEquals(song, comment.getSong());
        assertEquals(commenter, comment.getCommenter());
        assertEquals("This is a comment.", comment.getCommentText());
        assertEquals(timestamp, comment.getTimestamp());
    }

}
