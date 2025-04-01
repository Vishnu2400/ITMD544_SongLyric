package com.vlingampally.ITMD544_SongLyric.controllers;

import com.vlingampally.ITMD544_SongLyric.dto.SuggestionDTO;
import com.vlingampally.ITMD544_SongLyric.model.Suggestion;
import com.vlingampally.ITMD544_SongLyric.service.SuggestionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/suggestions")
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @PostMapping("/add/{songId}")
    public String addSuggestion(@PathVariable Long songId, @RequestBody Suggestion suggestion, Authentication authentication) {
        return suggestionService.addSuggestion(songId, suggestion, authentication);
    }

    @PutMapping("/modify/{suggestionId}")
    public String modifySuggestion(@PathVariable Long suggestionId, @RequestBody Suggestion newSuggestion, Authentication authentication) {
        return suggestionService.modifySuggestion(suggestionId, newSuggestion.getSuggestionText(), authentication);
    }

    @DeleteMapping("/delete/{suggestionId}")
    public String deleteSuggestion(@PathVariable Long suggestionId, Authentication authentication) {
        return suggestionService.deleteSuggestion(suggestionId, authentication);
    }

    @GetMapping("/all")
    public List<SuggestionDTO> getAllSuggestions() {
        return suggestionService.getAllSuggestions().stream().map(this::convertToDTO).collect(Collectors.toList());
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