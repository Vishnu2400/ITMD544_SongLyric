package com.vlingampally.ITMD544_SongLyric.service;

import com.vlingampally.ITMD544_SongLyric.model.Role;
import com.vlingampally.ITMD544_SongLyric.model.Song;
import com.vlingampally.ITMD544_SongLyric.model.Suggestion;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import com.vlingampally.ITMD544_SongLyric.repositories.SongRepository;
import com.vlingampally.ITMD544_SongLyric.repositories.SuggestionRepository;
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
    private final SuggestionRepository suggestionRepository;
    private final HiveAIService hiveAIService;

    public SongService(SongRepository songRepository, UserRepository userRepository, SuggestionRepository suggestionRepository, HiveAIService hiveAIService) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.suggestionRepository = suggestionRepository;
        this.hiveAIService = hiveAIService;
    }

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    //get songs by user id
    public List<Song> getSongsByUser(Users user) {
        if (user == null) {
            return List.of(); // Return an empty list if user is null
        }
        return songRepository.findByAuthor(user);
    }

    //get song by id for a user
    public Optional<Song> getSongById(Long id) {
        // Return the song if it exists, otherwise return an empty Optional
        return songRepository.findById(id);
    }



    @Transactional
    public String getSongTitleSuggestion(String lyrics) {
        return hiveAIService.getSongTitleSuggestion(lyrics);
    }

    @Transactional
    public String createSong(Song song,  Authentication authentication) {
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

        songRepository.save(song);
        return "Song created successfully ";
    }

    @Transactional
    public String updateSong(Long songId, Song updatedSong, Authentication authentication) {
        Optional<Users> userOptional = userRepository.findByUsername(authentication.getName());
        Optional<Song> songOptional = songRepository.findById(songId);

        if (userOptional.isEmpty() || songOptional.isEmpty()) {
            return "User or song not found!";
        }

        Users user = userOptional.get();
        Song song = songOptional.get();

        if (!user.getRoles().contains(Role.SONG_WRITER) || !song.getAuthor().equals(user)) {
            return "Permission denied. Only the author can update the song.";
        }

        // Update the song details
        song.setTitle(updatedSong.getTitle());
        song.setLyrics(updatedSong.getLyrics());
        song.setUpdatedAt(LocalDateTime.now());
        songRepository.save(song);
        return "Song updated successfully!";
    }
    @Transactional
    public String addLike(Long songId, Authentication authentication) {
        Optional<Users> userOptional = userRepository.findByUsername(authentication.getName());
        Optional<Song> songOptional = songRepository.findById(songId);

        if (userOptional.isEmpty() || songOptional.isEmpty()) {
            return "User or song not found!";
        }

        Users user = userOptional.get();
        Song song = songOptional.get();

        if (!user.getRoles().contains(Role.CONTRIBUTOR) && !user.getRoles().contains(Role.SONG_WRITER)) {
            return "Permission denied. Only contributors and song writers can like songs.";
        }

        song.setLikesCount(song.getLikesCount() + 1);
        songRepository.save(song);
        return "Song liked!";
    }

    @Transactional
    public String deleteSong(Long songId, Authentication authentication) {
        Optional<Users> userOptional = userRepository.findByUsername(authentication.getName());
        Optional<Song> songOptional = songRepository.findById(songId);

        if (userOptional.isEmpty() || songOptional.isEmpty()) {
            return "User or song not found!";
        }

        Users user = userOptional.get();
        Song song = songOptional.get();

        if (!user.getRoles().contains(Role.SONG_WRITER) || !song.getAuthor().equals(user)) {
            return "Permission denied. Only the author can delete the song.";
        }

        songRepository.delete(song);
        return "Song deleted successfully!";
    }
}