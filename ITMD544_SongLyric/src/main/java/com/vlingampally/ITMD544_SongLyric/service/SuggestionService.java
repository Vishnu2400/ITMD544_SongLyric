package com.vlingampally.ITMD544_SongLyric.service;

import com.vlingampally.ITMD544_SongLyric.repositories.SuggestionRepository;
import com.vlingampally.ITMD544_SongLyric.model.Suggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuggestionService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    public List<Suggestion> getAllSuggestions() {
        return suggestionRepository.findAll();
    }

    public Optional<Suggestion> getSuggestionById(Long id) {
        return suggestionRepository.findById(id);
    }

    public Suggestion saveSuggestion(Suggestion suggestion) {
        return suggestionRepository.save(suggestion);
    }

    public void deleteSuggestion(Long id) {
        suggestionRepository.deleteById(id);
    }
}
