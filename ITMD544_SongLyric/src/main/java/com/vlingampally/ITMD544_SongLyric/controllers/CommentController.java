package com.vlingampally.ITMD544_SongLyric.controllers;

import com.vlingampally.ITMD544_SongLyric.dto.CommentDTO;
import com.vlingampally.ITMD544_SongLyric.model.Comment;
import com.vlingampally.ITMD544_SongLyric.service.CommentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/add/{songId}")
    public String addComment(@PathVariable Long songId, @RequestBody Comment comment, Authentication authentication) {
        return commentService.addComment(songId, comment, authentication);
    }

    @PutMapping("/modify/{commentId}")
    public String modifyComment(@PathVariable Long commentId, @RequestBody Comment newComment, Authentication authentication) {
        return commentService.modifyComment(commentId, newComment.getCommentText(), authentication);
    }

    @GetMapping("/song/{songId}")
    public List<CommentDTO> getCommentsForSong(@PathVariable Long songId) {
        return commentService.getCommentsForSong(songId);
    }

    @DeleteMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId, Authentication authentication) {
        return commentService.deleteComment(commentId, authentication);
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setSongTitle(comment.getSong().getTitle());
        commentDTO.setCommenterUsername(comment.getCommenter().getUsername());
        commentDTO.setCommentText(comment.getCommentText());
        commentDTO.setTimestamp(comment.getTimestamp());
        return commentDTO;
    }
}