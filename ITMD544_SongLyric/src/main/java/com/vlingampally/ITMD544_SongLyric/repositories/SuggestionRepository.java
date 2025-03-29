package com.vlingampally.ITMD544_SongLyric.repositories;

import com.vlingampally.ITMD544_SongLyric.model.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    List<Suggestion> findAllBySongId(Long songId);
}
