package com.vlingampally.ITMD544_SongLyric.repositories;

import com.vlingampally.ITMD544_SongLyric.model.Song;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByAuthor(Users author);
}
