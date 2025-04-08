package com.vlingampally.ITMD544_SongLyric.resolvers;

import com.vlingampally.ITMD544_SongLyric.dto.*;
import com.vlingampally.ITMD544_SongLyric.service.*;
import com.vlingampally.ITMD544_SongLyric.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class QueryResolver {

    @Autowired
    private SongService songService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SuggestionService suggestionService;

    @Autowired
    private UserService userService;

    @QueryMapping
    public List<SongDTO> getAllSongs() {
        List<Song> songs = songService.getAllSongs();
        // Convert each Song to SongDTO
        return songs.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @QueryMapping
    public Optional<SongDTO> getSongById(@Argument Long id) {
        Optional<Song> song = songService.getSongById(id);
        return song.map(this::convertToDto);
    }

    //get all my songs
    @QueryMapping
    public List<SongDTO> getMySongs(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("No authenticated user found.");
        }

        Users user = userService.getCurrentUser(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Song> songs = songService.getSongsByUser(user);
        return songs.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @QueryMapping
    public String getSongTitleSuggestion(@Argument LyricsRequest request) {
        String songTitles = songService.getSongTitleSuggestion(request.getLyrics());

        // Remove unwanted newlines and additional spaces
        String cleanTitles = songTitles.replaceAll("\\n|\\r", " ");  // Replace all newline characters (both \n and \r) with a space
        cleanTitles = cleanTitles.replaceAll("\\s{2,}", " ").trim();  // Replace multiple spaces with a single space

        return cleanTitles;
    }


    @QueryMapping
    public List<CommentDTO> getCommentsForSong(@Argument Long songId) {
        return commentService.getCommentsForSong(songId);
    }

    @QueryMapping
    public List<SuggestionDTO> getAllSuggestions() {
        List<Suggestion> suggestions = suggestionService.getAllSuggestions();
        return suggestions.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @QueryMapping
    public List<SuggestionDTO> getSuggestionsForSong(@Argument Long songId) {
        List<Suggestion> suggestions = suggestionService.findAllBySongId(songId);
        return suggestions.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private SongDTO convertToDto(Song song) {
        SongDTO songDto = new SongDTO();
        songDto.setId(song.getId());
        songDto.setTitle(song.getTitle());
        songDto.setLyrics(song.getLyrics());
        songDto.setAuthorId(song.getAuthor().getId());
        songDto.setAuthorUsername(song.getAuthor().getUsername());
        songDto.setLikesCount(song.getLikesCount());
        songDto.setCreatedAt(song.getCreatedAt());
        songDto.setUpdatedAt(song.getUpdatedAt());

        // Fetch and set comments
        List<CommentDTO> comments = commentService.getCommentsForSong(songDto.getId());
        songDto.setComments(comments);

        // Fetch and set suggestions
        List<SuggestionDTO> suggestions = suggestionService.getSuggestionsForSong(song.getId());

        songDto.setSuggestions(suggestions);

        return songDto;
    }


    private CommentDTO convertToDto(Comment comment) {
        CommentDTO commentDto = new CommentDTO();
        commentDto.setId(comment.getId());
        commentDto.setSongTitle(comment.getSong().getTitle());
        commentDto.setCommenterUsername(comment.getCommenter().getUsername());
        commentDto.setCommentText(comment.getCommentText());
        commentDto.setTimestamp(comment.getTimestamp());
        return commentDto;
    }

    private SuggestionDTO convertToDto(Suggestion suggestion) {
        SuggestionDTO suggestionDto = new SuggestionDTO();
        suggestionDto.setId(suggestion.getId());
        suggestionDto.setSongTitle(suggestion.getSong().getTitle());
        suggestionDto.setSuggesterUsername(suggestion.getSuggester().getUsername());
        suggestionDto.setSuggestionText(suggestion.getSuggestionText());
        suggestionDto.setTimestamp(suggestion.getTimestamp());
        return suggestionDto;
    }
}