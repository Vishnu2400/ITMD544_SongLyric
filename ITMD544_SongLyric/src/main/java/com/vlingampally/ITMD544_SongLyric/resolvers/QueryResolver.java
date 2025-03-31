package com.vlingampally.ITMD544_SongLyric.resolvers;

import com.vlingampally.ITMD544_SongLyric.dto.CommentDTO;
import com.vlingampally.ITMD544_SongLyric.dto.SongDTO;
import com.vlingampally.ITMD544_SongLyric.dto.SuggestionDTO;
import com.vlingampally.ITMD544_SongLyric.dto.UserDTO;
import com.vlingampally.ITMD544_SongLyric.model.*;
import com.vlingampally.ITMD544_SongLyric.repositories.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.QueryMapping;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class QueryResolver {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final SuggestionRepository suggestionRepository;

    public QueryResolver(SongRepository songRepository, UserRepository userRepository, CommentRepository commentRepository, SuggestionRepository suggestionRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.suggestionRepository = suggestionRepository;
    }

    @QueryMapping
    public List<SongDTO> getAllSongs() {
        return songRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @QueryMapping
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @QueryMapping
    public UserDTO getCurrentUser(@AuthenticationPrincipal User user) {
        Optional<Users> currentUser = userRepository.findByUsername(user.getUsername());
        return currentUser.map(this::convertToDTO).orElse(null);
    }

    @QueryMapping
    public UserDTO getUserByUsername(String username) {
        Optional<Users> user = userRepository.findByUsername(username);
        return user.map(this::convertToDTO).orElse(null);
    }

    @QueryMapping
    public List<CommentDTO> getCommentsForSong(Long songId) {
        return commentRepository.findAllBySongId(songId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @QueryMapping
    public List<SuggestionDTO> getAllSuggestions() {
        return suggestionRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private SongDTO convertToDTO(Song song) {
        SongDTO songDTO = new SongDTO();
        songDTO.setId(song.getId());
        songDTO.setTitle(song.getTitle());
        songDTO.setLyrics(song.getLyrics());
        songDTO.setAuthorUsername(song.getAuthor().getUsername());
        songDTO.setLikesCount(song.getLikesCount());
        songDTO.setCreatedAt(song.getCreatedAt());
        songDTO.setUpdatedAt(song.getUpdatedAt());
        return songDTO;
    }

    private UserDTO convertToDTO(Users user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRoles(user.getRoles());
        return userDTO;
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

    private SuggestionDTO convertToDTO(Suggestion suggestion) {
        SuggestionDTO suggestionDTO = new SuggestionDTO();
        suggestionDTO.setId(suggestion.getId());
        suggestionDTO.setSongTitle(suggestion.getSong().getTitle());
        suggestionDTO.setSuggesterUsername(suggestion.getSuggester().getUsername());
        suggestionDTO.setSuggestionText(suggestion.getSuggestionText());
        suggestionDTO.setTimestamp(suggestion.getTimestamp());
        return suggestionDTO;
    }
}