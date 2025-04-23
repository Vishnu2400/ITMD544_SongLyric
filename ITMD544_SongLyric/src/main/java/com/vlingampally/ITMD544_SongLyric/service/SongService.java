package com.vlingampally.ITMD544_SongLyric.service;

import com.vlingampally.ITMD544_SongLyric.dto.CommentDTO;
import com.vlingampally.ITMD544_SongLyric.dto.SongDTO;
import com.vlingampally.ITMD544_SongLyric.dto.SuggestionDTO;
import com.vlingampally.ITMD544_SongLyric.exception.SongNotFoundException;
import com.vlingampally.ITMD544_SongLyric.model.*;
import com.vlingampally.ITMD544_SongLyric.repositories.CommentRepository;
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
import java.util.stream.Collectors;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final SuggestionRepository suggestionRepository;
    private final HiveAIService hiveAIService;
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final SuggestionService suggestionService;

    public SongService(SongRepository songRepository, UserRepository userRepository, SuggestionRepository suggestionRepository, HiveAIService hiveAIService, CommentRepository commentRepository, CommentService commentService, SuggestionService suggestionService) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.suggestionRepository = suggestionRepository;
        this.hiveAIService = hiveAIService;
        this.commentRepository = commentRepository;
        this.commentService = commentService;
        this.suggestionService = suggestionService;
    }

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    //get songs by user id
    public List<Song> getSongsByUser(Users user) {
        if (user == null) {
            return List.of(); // Return an empty list if user is null
        }
        Long authorId = user.getId();
        return songRepository.findByAuthorId(authorId);
    }

    //get song by id for a user
    public Optional<Song> getSongById(Long id) {
        // Return the song if it exists, otherwise return an empty Optional
        return songRepository.findById(id);
    }

    @Transactional
    public List<String> getSongTitleSuggestions(String lyrics) {
        return hiveAIService.getSongTitleSuggestions(lyrics);
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

        // Delete comments and suggestions related to the song
        List<Comment> comments = commentRepository.findAllBySongId(song.getId());
        commentRepository.deleteAll(comments);

        List<Suggestion> suggestions = suggestionRepository.findAllBySongId(song.getId());
        suggestionRepository.deleteAll(suggestions);

        // Delete the song itself
        songRepository.delete(song);

        return "Song and related data (comments, suggestions) deleted successfully!";
    }

    @Transactional
    public void deleteSongsByUser(Users user) {
        Long AuthorId = user.getId();
        List<Song> songs = songRepository.findByAuthorId(AuthorId);
        for (Song song : songs) {
            // Delete comments related to the song
            List<Comment> comments = commentRepository.findAllBySongId(song.getId());
            commentRepository.deleteAll(comments);

            // Delete suggestions related to the song
            List<Suggestion> suggestions = suggestionRepository.findAllBySongId(song.getId());
            suggestionRepository.deleteAll(suggestions);

            // Delete the song
            songRepository.delete(song);
        }
    }

    public List<SongDTO> getAllSongsWithComments() {
        List<Song> songs = songRepository.findAll();

        return songs.stream().map(song -> {
            SongDTO dto = mapToDTO(song);
            List<CommentDTO> comments = commentService.getCommentsForSong(song.getId());
            dto.setComments(comments);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<SongDTO> getAllSongsWithSuggestions() {
        List<Song> songs = songRepository.findAll();

        return songs.stream().map(song -> {
            SongDTO dto = mapToDTO(song);
            List<SuggestionDTO> suggestions = suggestionService.getSuggestionsForSong(song.getId());
            dto.setSuggestions(suggestions);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<SongDTO> getAllSongsWithCommentsAndSuggestions() {
        List<Song> songs = songRepository.findAll();

        return songs.stream().map(song -> {
            SongDTO dto = mapToDTO(song);
            dto.setAuthorId(song.getAuthor().getId());
            dto.setComments(commentService.getCommentsForSong(song.getId()));
            dto.setSuggestions(suggestionService.getSuggestionsForSong(song.getId()));
            return dto;
        }).collect(Collectors.toList());
    }

    public SongDTO getSongWithCommentsAndSuggestions(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new SongNotFoundException(songId));

        SongDTO dto = mapToDTO(song);
        dto.setComments(commentService.getCommentsForSong(songId));
        dto.setSuggestions(suggestionService.getSuggestionsForSong(songId));
        return dto;
    }


    private SongDTO mapToDTO(Song song) {
        SongDTO dto = new SongDTO();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setLyrics(song.getLyrics());
        dto.setAuthorUsername(song.getAuthor().getUsername());
        dto.setLikesCount(song.getLikesCount());
        dto.setCreatedAt(song.getCreatedAt());
        dto.setUpdatedAt(song.getUpdatedAt());
        return dto;
    }
}