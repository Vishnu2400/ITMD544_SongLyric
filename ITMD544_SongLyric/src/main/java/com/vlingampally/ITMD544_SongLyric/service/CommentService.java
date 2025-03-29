package com.vlingampally.ITMD544_SongLyric.service;

import com.vlingampally.ITMD544_SongLyric.model.Comment;
import com.vlingampally.ITMD544_SongLyric.model.Role;
import com.vlingampally.ITMD544_SongLyric.model.Song;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import com.vlingampally.ITMD544_SongLyric.repositories.CommentRepository;
import com.vlingampally.ITMD544_SongLyric.repositories.SongRepository;
import com.vlingampally.ITMD544_SongLyric.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, SongRepository songRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String addComment(Long songId, Comment comment, Authentication authentication) {
        Optional<Users> user = userRepository.findByUsername(authentication.getName());
        Optional<Song> song = songRepository.findById(songId);

        if (user.isEmpty() || song.isEmpty() || !user.get().getRoles().contains(Role.CONTRIBUTOR) && !user.get().getRoles().contains(Role.SONG_WRITER)) {
            return "Permission denied. Only contributors and song writers can add comments.";
        }

        comment.setSong(song.get());
        comment.setCommenter(user.get());
        comment.setTimestamp(LocalDateTime.now()); // Set the current timestamp
        commentRepository.save(comment);
        return "Comment added!";
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsForSong(Long songId) {
        return commentRepository.findAllBySongId(songId);
    }
}