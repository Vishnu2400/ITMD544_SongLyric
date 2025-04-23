package com.vlingampally.ITMD544_SongLyric.resolvers;

import com.vlingampally.ITMD544_SongLyric.dto.UserDTO;
import com.vlingampally.ITMD544_SongLyric.model.*;
import com.vlingampally.ITMD544_SongLyric.repositories.SongRepository;
import com.vlingampally.ITMD544_SongLyric.repositories.SuggestionRepository;
import com.vlingampally.ITMD544_SongLyric.repositories.UserRepository;
import com.vlingampally.ITMD544_SongLyric.security.JwtService;
import com.vlingampally.ITMD544_SongLyric.service.AuthService;
import com.vlingampally.ITMD544_SongLyric.service.SongService;
import com.vlingampally.ITMD544_SongLyric.service.CommentService;
import com.vlingampally.ITMD544_SongLyric.service.SuggestionService;
import com.vlingampally.ITMD544_SongLyric.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class MutationResolver {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final SuggestionRepository suggestionRepository;
    private final SongService songService;
    private final CommentService commentService;
    private final SuggestionService suggestionService;
    private final UserService userService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public MutationResolver(SongRepository songRepository, UserRepository userRepository, SuggestionRepository suggestionRepository,
                            SongService songService, CommentService commentService, SuggestionService suggestionService,
                            UserService userService, AuthService authService, PasswordEncoder passwordEncoder,
                            AuthenticationManager authenticationManager, JwtService jwtService) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.suggestionRepository = suggestionRepository;
        this.songService = songService;
        this.commentService = commentService;
        this.suggestionService = suggestionService;
        this.userService = userService;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @MutationMapping
    public Map<String, String> register(@Argument String username, @Argument String email, @Argument String password, @Argument String roleStr) {
        return authService.register(username, email, password, roleStr);
    }

    @MutationMapping
    public Map<String, String> login(@Argument String username, @Argument String password) {
        return authService.login(username, password);
    }

    @MutationMapping
    public String createSong(@Argument String title, @Argument String lyrics, Authentication authentication) {
        Song song = new Song();
        song.setTitle(title);
        song.setLyrics(lyrics);

        return songService.createSong(song, authentication);
    }

    @MutationMapping
    public String updateSong(@Argument Long id, @Argument String title, @Argument String lyrics, Authentication authentication) {
        Song updatedSong = new Song();
        updatedSong.setTitle(title);
        updatedSong.setLyrics(lyrics);
        return songService.updateSong(id, updatedSong, authentication);
    }

    @MutationMapping
    public String deleteSong(@Argument Long id, Authentication authentication) {
        return songService.deleteSong(id, authentication);
    }

    @MutationMapping
    public String addComment(@Argument Long songId, @Argument String commentText, Authentication authentication) {
        Comment comment = new Comment();
        comment.setCommentText(commentText);
        return commentService.addComment(songId, comment, authentication);
    }

    @MutationMapping
    public String modifyComment(@Argument Long commentId, @Argument String commentText, Authentication authentication) {
        return commentService.modifyComment(commentId, commentText, authentication);
    }

    @MutationMapping
    public String deleteComment(@Argument Long commentId, Authentication authentication) {
        return commentService.deleteComment(commentId, authentication);
    }

    @MutationMapping
    public ResponseEntity<String> addSuggestion(@Argument Long songId, @Argument String suggestionText, Authentication authentication) {
        Suggestion suggestion = new Suggestion();
        suggestion.setSuggestionText(suggestionText);
        return suggestionService.addSuggestion(songId, suggestion, authentication);
    }

    @MutationMapping
    public String modifySuggestion(@Argument Long suggestionId, @Argument String suggestionText, Authentication authentication) {
        return suggestionService.modifySuggestion(suggestionId, suggestionText, authentication);
    }

    @MutationMapping
    public String deleteSuggestion(@Argument Long suggestionId, Authentication authentication) {
        return suggestionService.deleteSuggestion(suggestionId, authentication);
    }

    @MutationMapping
    public UserDTO addRoleToUser(@Argument String username, @Argument String role) {
        Users user = userService.addRoleToUser(username, Role.valueOf(role));
        return convertToDTO(user);
    }

    @MutationMapping
    public UserDTO removeRoleFromUser(@Argument String username, @Argument String role) {
        Users user = userService.removeRoleFromUser(username, Role.valueOf(role));
        return convertToDTO(user);
    }

    @MutationMapping
    public UserDTO updateUserName(@Argument String username, @Argument String newName) {
        Users user = userService.updateUserName(username, newName);
        return convertToDTO(user);
    }

    @MutationMapping
    public UserDTO updateUserEmail(@Argument String username, @Argument String newEmail) {
        Users user = userService.updateUserEmail(username, newEmail);
        return convertToDTO(user);
    }

    @MutationMapping
    public UserDTO updateUserPassword(@Argument String username, @Argument String newPassword) {
        Users user = userService.updateUserPassword(username, newPassword);
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(Users user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRoles(user.getRoles());
        return userDTO;
    }
}