package com.vlingampally.ITMD544_SongLyric.resolvers;

import com.vlingampally.ITMD544_SongLyric.dto.LyricsRequest;
import com.vlingampally.ITMD544_SongLyric.dto.UserDTO;
import com.vlingampally.ITMD544_SongLyric.model.*;
import com.vlingampally.ITMD544_SongLyric.repositories.SongRepository;
import com.vlingampally.ITMD544_SongLyric.repositories.SuggestionRepository;
import com.vlingampally.ITMD544_SongLyric.repositories.UserRepository;
import com.vlingampally.ITMD544_SongLyric.security.JwtService;
import com.vlingampally.ITMD544_SongLyric.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
public class MutationResolver {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final SuggestionRepository suggestionRepository;
    private final SongService songService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public MutationResolver(SongRepository songRepository, UserRepository userRepository, SuggestionRepository suggestionRepository, SongService songService,
                            PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.suggestionRepository = suggestionRepository;
        this.songService = songService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @MutationMapping
    public String register(String username, String email, String password, String roleStr) {
        if (userRepository.findByUsername(username).isPresent()) {
            return "Username already taken";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return "Email already in use";
        }

        Set<Role> roles = new HashSet<>();
        if (roleStr != null) {
            try {
                roles.add(Role.valueOf(roleStr));
            } catch (IllegalArgumentException e) {
                return "Invalid role provided";
            }
        } else {
            roles.add(Role.CONTRIBUTOR);
        }

        Users user = new Users();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);

        userRepository.save(user);

        return "User registered successfully";
    }

    @MutationMapping
    public String login(AuthenticationRequest authenticationRequest) {
        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return token;
    }

    @MutationMapping
    public UserDTO updateUserDetails(Authentication authentication, String email, String password) {
        Optional<Users> userOpt = userRepository.findByUsername(authentication.getName());
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));  // password should be encrypted before updating
            userRepository.save(user);
            return convertToDTO(user);
        }
        return null;
    }

    @MutationMapping
    public UserDTO addRoleToUser(String username, String roleStr) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            try {
                Role role = Role.valueOf(roleStr);
                user.getRoles().add(role);
                userRepository.save(user);
                return convertToDTO(user);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role provided");
            }
        }
        return null;
    }

    @MutationMapping
    public UserDTO removeRoleFromUser(String username, String roleStr) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            try {
                Role role = Role.valueOf(roleStr);
                user.getRoles().remove(role);
                userRepository.save(user);
                return convertToDTO(user);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role provided");
            }
        }
        return null;
    }

    @MutationMapping
    public UserDTO modifyUserRole(String username, String roleStr) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.getRoles().clear();  // Clear existing roles
            try {
                Role role = Role.valueOf(roleStr);
                user.getRoles().add(role);  // Add the new role
                userRepository.save(user);
                return convertToDTO(user);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role provided");
            }
        }
        return null;
    }

    @MutationMapping
    public Boolean deleteUser(String username) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());  // delete user if found
            return true;
        }
        return false;
    }

    @MutationMapping
    public String likeSong(Long songId, Authentication authentication) {
        Optional<Users> userOpt = userRepository.findByUsername(authentication.getName());
        Optional<Song> songOpt = songRepository.findById(songId);

        if (userOpt.isEmpty() || songOpt.isEmpty() ||
                (!userOpt.get().getRoles().contains(Role.CONTRIBUTOR) && !userOpt.get().getRoles().contains(Role.SONG_WRITER))) {
            return "Permission denied. Only contributors and song writers can like songs.";
        }

        Song existingSong = songOpt.get();
        existingSong.setLikesCount(existingSong.getLikesCount() + 1);
        songRepository.save(existingSong);
        return "Song liked!";
    }

    @MutationMapping
    public String suggestTitle(LyricsRequest lyricsRequest) {
        return songService.getSongTitleSuggestion(lyricsRequest.getLyrics());
    }

    @MutationMapping
    public String modifySuggestion(Long suggestionId, String suggestionText, Authentication authentication) {
        Optional<Users> userOpt = userRepository.findByUsername(authentication.getName());
        Optional<Suggestion> suggestionOpt = suggestionRepository.findById(suggestionId);

        if (userOpt.isEmpty() || suggestionOpt.isEmpty() || !userOpt.get().getRoles().contains(Role.CONTRIBUTOR)) {
            return "Permission denied. Only contributors can modify suggestions.";
        }

        Suggestion suggestion = suggestionOpt.get();
        suggestion.setSuggestionText(suggestionText);
        suggestion.setTimestamp(LocalDateTime.now());  // Update the timestamp
        suggestionRepository.save(suggestion);
        return "Suggestion modified!";
    }

    @MutationMapping
    public String deleteSuggestion(Long suggestionId, Authentication authentication) {
        Optional<Users> userOpt = userRepository.findByUsername(authentication.getName());
        Optional<Suggestion> suggestionOpt = suggestionRepository.findById(suggestionId);

        if (userOpt.isEmpty() || suggestionOpt.isEmpty() || !userOpt.get().getRoles().contains(Role.CONTRIBUTOR)) {
            return "Permission denied. Only contributors can delete suggestions.";
        }

        suggestionRepository.delete(suggestionOpt.get());
        return "Suggestion deleted!";
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