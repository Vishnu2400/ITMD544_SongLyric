package com.vlingampally.ITMD544_SongLyric.service;

import com.vlingampally.ITMD544_SongLyric.model.Role;
import com.vlingampally.ITMD544_SongLyric.model.Song;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import com.vlingampally.ITMD544_SongLyric.repositories.SongRepository;
import com.vlingampally.ITMD544_SongLyric.repositories.UserRepository;
import com.vlingampally.ITMD544_SongLyric.service.HiveAIService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final HiveAIService hiveAIService;

    public SongService(SongRepository songRepository, UserRepository userRepository, HiveAIService hiveAIService) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.hiveAIService = hiveAIService;
    }

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Optional<Song> getSongById(Long id) {
        return songRepository.findById(id);
    }

    public Song saveSong(Song song) {
        return songRepository.save(song);
    }

    @Transactional
    public void deleteSong(Long id) {
        songRepository.deleteById(id);
    }

    public List<Song> getSongsByUser(Users user) {
        return songRepository.findByAuthor(user);
    }

    @Transactional
    public String getSongTitleSuggestion(String lyrics) {
        return hiveAIService.getSongTitleSuggestion(lyrics);
    }

    @Transactional
    public String createSong(Song song, String chosenName, Authentication authentication) {
        Optional<Users> userOptional = userRepository.findByUsername(authentication.getName());

        if (userOptional.isEmpty()) {
            return "User not found!";
        }

        Users user = userOptional.get();

        if (!user.getRoles().contains(Role.SONG_WRITER)) {
            return "Permission denied. Only song writers can create songs.";
        }

        // Check if all required fields are present
        if (song.getTitle() == null || song.getTitle().isEmpty()) {
            return "Title is required.";
        }

        if (song.getLyrics() == null || song.getLyrics().isEmpty()) {
            return "Lyrics are required.";
        }

        song.setAuthor(user);
        song.setCreatedAt(LocalDateTime.now());
        song.setUpdatedAt(LocalDateTime.now());
        song.setChosenName(chosenName);

        songRepository.save(song);
        return "Song created successfully with chosen name: " + chosenName;
    }
}