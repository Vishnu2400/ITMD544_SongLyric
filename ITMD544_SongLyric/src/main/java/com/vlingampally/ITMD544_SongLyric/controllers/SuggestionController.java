package com.vlingampally.ITMD544_SongLyric.controllers;

import com.vlingampally.ITMD544_SongLyric.dto.SuggestionDTO;
import com.vlingampally.ITMD544_SongLyric.model.*;
import com.vlingampally.ITMD544_SongLyric.repositories.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/suggestions")
public class SuggestionController {

    private final SuggestionRepository suggestionRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public SuggestionController(SuggestionRepository suggestionRepository, SongRepository songRepository, UserRepository userRepository) {
        this.suggestionRepository = suggestionRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/add/{songId}")
    public String addSuggestion(@PathVariable Long songId, @RequestBody Suggestion suggestion, Authentication authentication) {
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

    @PutMapping("/modify/{suggestionId}")
    public String modifySuggestion(@PathVariable Long suggestionId, @RequestBody Suggestion newSuggestion, Authentication authentication) {
        Optional<Users> user = userRepository.findByUsername(authentication.getName());
        Optional<Suggestion> existingSuggestion = suggestionRepository.findById(suggestionId);

        if (user.isEmpty() || existingSuggestion.isEmpty() || !user.get().getRoles().contains(Role.CONTRIBUTOR)) {
            return "Permission denied. Only contributors can modify suggestions.";
        }

        Suggestion suggestion = existingSuggestion.get();
        suggestion.setSuggestionText(newSuggestion.getSuggestionText());
        suggestion.setTimestamp(LocalDateTime.now()); // Update the timestamp
        suggestionRepository.save(suggestion);
        return "Suggestion modified!";
    }

    @DeleteMapping("/delete/{suggestionId}")
    public String deleteSuggestion(@PathVariable Long suggestionId, Authentication authentication) {
        Optional<Users> user = userRepository.findByUsername(authentication.getName());
        Optional<Suggestion> suggestion = suggestionRepository.findById(suggestionId);

        if (user.isEmpty() || suggestion.isEmpty() || !user.get().getRoles().contains(Role.CONTRIBUTOR)) {
            return "Permission denied. Only contributors can delete suggestions.";
        }

        suggestionRepository.delete(suggestion.get());
        return "Suggestion deleted!";
    }

    @GetMapping("/all")
    public List<SuggestionDTO> getAllSuggestions() {
        return suggestionRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
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