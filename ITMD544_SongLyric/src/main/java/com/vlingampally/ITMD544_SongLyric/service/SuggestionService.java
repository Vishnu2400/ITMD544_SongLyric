package com.vlingampally.ITMD544_SongLyric.service;

import com.vlingampally.ITMD544_SongLyric.model.Role;
import com.vlingampally.ITMD544_SongLyric.model.Song;
import com.vlingampally.ITMD544_SongLyric.model.Suggestion;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import com.vlingampally.ITMD544_SongLyric.repositories.SuggestionRepository;
import com.vlingampally.ITMD544_SongLyric.repositories.SongRepository;
import com.vlingampally.ITMD544_SongLyric.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public SuggestionService(SuggestionRepository suggestionRepository, SongRepository songRepository, UserRepository userRepository) {
        this.suggestionRepository = suggestionRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    public List<Suggestion> getAllSuggestions() {
        return suggestionRepository.findAll();
    }

    //get suggestion of song bu song id
    public List<Suggestion> findAllBySongId(Long songId) {
        // Return all suggestions for a given song ID
        return suggestionRepository.findAllBySongId(songId);
    }

    public String addSuggestion(Long songId, Suggestion suggestion, Authentication authentication) {
        Optional<Users> user = userRepository.findByUsername(authentication.getName());
        Optional<Song> song = songRepository.findById(songId);

        if (user.isEmpty() || song.isEmpty() || !user.get().getRoles().contains(Role.CONTRIBUTOR)) {
            return "Permission denied. Only contributors can add suggestions.";
        }

        suggestion.setSong(song.get());
        suggestion.setSuggester(user.get());
        suggestion.setTimestamp(LocalDateTime.now()); // Set the current timestamp
        suggestionRepository.save(suggestion);
        return "Suggestion added!";
    }

    public String modifySuggestion(Long suggestionId, String suggestionText, Authentication authentication) {
        Optional<Users> user = userRepository.findByUsername(authentication.getName());
        Optional<Suggestion> existingSuggestion = suggestionRepository.findById(suggestionId);

        if (user.isEmpty() || existingSuggestion.isEmpty() || !user.get().getRoles().contains(Role.CONTRIBUTOR)) {
            return "Permission denied. Only contributors can modify suggestions.";
        }

        Suggestion suggestion = existingSuggestion.get();
        suggestion.setSuggestionText(suggestionText);
        suggestion.setTimestamp(LocalDateTime.now()); // Update the timestamp
        suggestionRepository.save(suggestion);
        return "Suggestion modified!";
    }

    public String deleteSuggestion(Long suggestionId, Authentication authentication) {
        Optional<Users> user = userRepository.findByUsername(authentication.getName());
        Optional<Suggestion> suggestion = suggestionRepository.findById(suggestionId);

        if (user.isEmpty() || suggestion.isEmpty() || !user.get().getRoles().contains(Role.CONTRIBUTOR)) {
            return "Permission denied. Only contributors can delete suggestions.";
        }

        suggestionRepository.delete(suggestion.get());
        return "Suggestion deleted!";
    }
}