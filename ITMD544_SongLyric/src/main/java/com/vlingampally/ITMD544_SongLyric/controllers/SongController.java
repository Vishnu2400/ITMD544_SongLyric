package com.vlingampally.ITMD544_SongLyric.controllers;

import com.vlingampally.ITMD544_SongLyric.dto.LyricsRequest;
import com.vlingampally.ITMD544_SongLyric.dto.SongDTO;
import com.vlingampally.ITMD544_SongLyric.model.Song;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import com.vlingampally.ITMD544_SongLyric.service.SongService;
import com.vlingampally.ITMD544_SongLyric.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;
    private final UserService userService;

    public SongController(SongService songService, UserService userService) {
        this.songService = songService;
        this.userService = userService;
    }

    @GetMapping("/my-songs")
    public List<Song> getMySongs(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("No authenticated user found.");
        }

        // Fetch user from database
        Users user = userService.getCurrentUser(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch songs created by the user
        return songService.getSongsByUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        Optional<Song> song = songService.getSongById(id);
        return song.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Create a new song (only for SONG_WRITER role)
    @PostMapping("/create")
    @Transactional
    public String createSong(@RequestBody Song song, Authentication authentication) {
        return songService.createSong(song, authentication);
    }

    // Endpoint to get song title suggestions based on lyrics
    @PostMapping("/suggest-title")
    @Transactional
    public List<String> getSongTitleSuggestions(@RequestBody LyricsRequest request) {
        return songService.getSongTitleSuggestions(request.getLyrics());
    }


    // Get all songs (visible to everyone)
    @GetMapping("/all")
    @Transactional(readOnly = true)
    public List<SongDTO> getAllSongs() {
        return songService.getAllSongs().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/with-comments")
    public ResponseEntity<List<SongDTO>> getAllSongsWithComments() {
        return ResponseEntity.ok(songService.getAllSongsWithComments());
    }

    @GetMapping("/with-suggestions")
    public ResponseEntity<List<SongDTO>> getAllSongsWithSuggestions() {
        return ResponseEntity.ok(songService.getAllSongsWithSuggestions());
    }

    @GetMapping("/with-comments-suggestions")
    public ResponseEntity<List<SongDTO>> getAllSongsWithCommentsAndSuggestions() {
        return ResponseEntity.ok(songService.getAllSongsWithCommentsAndSuggestions());
    }

    @GetMapping("/{songId}/with-comments-suggestions")
    public ResponseEntity<SongDTO> getSongWithCommentsAndSuggestions(@PathVariable Long songId) {
        return ResponseEntity.ok(songService.getSongWithCommentsAndSuggestions(songId));
    }



    // Update a song (only for SONG_WRITER role)
    @PutMapping("/update/{songId}")
    @Transactional
    public String updateSong(@PathVariable Long songId, @RequestBody Song updatedSong, Authentication authentication) {
        return songService.updateSong(songId, updatedSong, authentication);
    }

    // Add a like to a song
    @PostMapping("/like/{songId}")
    public String addLike(@PathVariable Long songId, Authentication authentication) {
        return songService.addLike(songId, authentication);
    }

    @DeleteMapping("/delete/{songId}")
    @Transactional
    public String deleteSong(@PathVariable Long songId, Authentication authentication) {
        return songService.deleteSong(songId, authentication);
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