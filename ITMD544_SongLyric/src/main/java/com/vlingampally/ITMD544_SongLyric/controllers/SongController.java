package com.vlingampally.ITMD544_SongLyric.controllers;

import com.vlingampally.ITMD544_SongLyric.dto.LyricsRequest;
import com.vlingampally.ITMD544_SongLyric.dto.SongDTO;
import com.vlingampally.ITMD544_SongLyric.model.Role;
import com.vlingampally.ITMD544_SongLyric.model.Song;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import com.vlingampally.ITMD544_SongLyric.repositories.SongRepository;
import com.vlingampally.ITMD544_SongLyric.repositories.UserRepository;
import com.vlingampally.ITMD544_SongLyric.service.SongService;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final SongService songService;

    public SongController(SongRepository songRepository, UserRepository userRepository, SongService songService) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.songService = songService;
    }

    // Create a new song (only for SONG_WRITER role)
    @PostMapping("/create")
    @Transactional
    public String createSong(@RequestBody Song song, @RequestParam String chosenName, Authentication authentication) {
        return songService.createSong(song, chosenName, authentication);
    }

    // Endpoint to get song title suggestions based on lyrics
    @PostMapping("/suggest-title")
    @Transactional
    public String getSongTitleSuggestion(@RequestBody LyricsRequest request) {
        return songService.getSongTitleSuggestion(request.getLyrics());
    }

    // Get all songs (visible to everyone)
    @GetMapping("/all")
    @Transactional(readOnly = true)
    public List<SongDTO> getAllSongs() {
        return songService.getAllSongs().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Update a song (only for SONG_WRITER role)
    @PutMapping("/update/{songId}")
    @Transactional
    public String updateSong(@PathVariable Long songId, @RequestBody Song updatedSong, Authentication authentication) {
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

    // Add a like to a song
    @PostMapping("/like/{songId}")
    public String addLike(@PathVariable Long songId, Authentication authentication) {
        Optional<Users> user = userRepository.findByUsername(authentication.getName());
        Optional<Song> song = songRepository.findById(songId);

        if (user.isEmpty() || song.isEmpty() || !user.get().getRoles().contains(Role.CONTRIBUTOR) && !user.get().getRoles().contains(Role.SONG_WRITER)) {
            return "Permission denied. Only contributors and song writers can like songs.";
        }

        Song existingSong = song.get();
        existingSong.setLikesCount(existingSong.getLikesCount() + 1);
        songRepository.save(existingSong);
        return "Song liked!";
    }

    private SongDTO convertToDTO(Song song) {
        SongDTO songDTO = new SongDTO();
        songDTO.setId(song.getId());
        songDTO.setTitle(song.getTitle());
        songDTO.setLyrics(song.getLyrics());
        songDTO.setAuthorUsername(song.getAuthor().getUsername());
        songDTO.setLikesCount(song.getLikesCount());
        songDTO.setCreatedAt(song.getCreatedAt());
        songDTO.setUpdatedAt(song.getUpdatedAt());
        return songDTO;
    }
}