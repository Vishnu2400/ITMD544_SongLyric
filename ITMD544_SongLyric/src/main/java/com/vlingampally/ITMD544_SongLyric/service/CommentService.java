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
        Optional<Users> userOptional = userRepository.findByUsername(authentication.getName());
        Optional<Song> songOptional = songRepository.findById(songId);

        if (userOptional.isEmpty() || songOptional.isEmpty() ||
                (!userOptional.get().getRoles().contains(Role.CONTRIBUTOR) && !userOptional.get().getRoles().contains(Role.SONG_WRITER))) {
            return "Permission denied. Only contributors and song writers can add comments.";
        }

        comment.setSong(songOptional.get());
        comment.setCommenter(userOptional.get());
        comment.setTimestamp(LocalDateTime.now()); // Set the current timestamp
        commentRepository.save(comment);
        return "Comment added!";
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsForSong(Long songId) {
        return commentRepository.findAllBySongId(songId);
    }

    @Transactional
    public String modifyComment(Long commentId, String commentText, Authentication authentication) {
        Optional<Users> userOptional = userRepository.findByUsername(authentication.getName());
        Optional<Comment> commentOptional = commentRepository.findById(commentId);

        if (userOptional.isEmpty() || commentOptional.isEmpty() ||
                (!userOptional.get().getRoles().contains(Role.CONTRIBUTOR) && !userOptional.get().getRoles().contains(Role.SONG_WRITER))) {
            return "Permission denied. Only contributors and song writers can modify comments.";
        }

        Comment comment = commentOptional.get();
        if (!comment.getCommenter().equals(userOptional.get())) {
            return "Permission denied. Only the author can modify the comment.";
        }

        comment.setCommentText(commentText);
        comment.setTimestamp(LocalDateTime.now()); // Update the timestamp
        commentRepository.save(comment);
        return "Comment modified successfully!";
    }

    @Transactional
    public String deleteComment(Long commentId, Authentication authentication) {
        Optional<Users> userOptional = userRepository.findByUsername(authentication.getName());
        Optional<Comment> commentOptional = commentRepository.findById(commentId);

        if (userOptional.isEmpty() || commentOptional.isEmpty() ||
                (!userOptional.get().getRoles().contains(Role.CONTRIBUTOR) && !userOptional.get().getRoles().contains(Role.SONG_WRITER))) {
            return "Permission denied. Only contributors and song writers can delete comments.";
        }

        Comment comment = commentOptional.get();
        if (!comment.getCommenter().equals(userOptional.get())) {
            return "Permission denied. Only the author can delete the comment.";
        }

        commentRepository.delete(comment);
        return "Comment deleted successfully!";
    }
}