package com.vlingampally.ITMD544_SongLyric.service;

import com.vlingampally.ITMD544_SongLyric.model.Role;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import com.vlingampally.ITMD544_SongLyric.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private SongService songService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private SuggestionService suggestionService;

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Users> getCurrentUser(String username) {
        return userRepository.findByUsername(username);
    }

    public Users updateUserDetails(String username, Users updatedUser) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.setEmail(updatedUser.getEmail());
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));  // password should be encrypted before updating
            return userRepository.save(user);
        }
        return null;  // return null if user is not found
    }

    public Users updateUserName(String username, String newName) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.setUsername(newName);
            return userRepository.save(user);
        }
        return null;
    }

    public Users updateUserEmail(String username, String newEmail) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.setEmail(newEmail);
            return userRepository.save(user);
        }
        return null;
    }

    public Users updateUserPassword(String username, String newPassword) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));  // password should be encrypted before updating
            return userRepository.save(user);
        }
        return null;
    }

    public Users addRoleToUser(String username, Role role) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.getRoles().add(role);
            return userRepository.save(user);
        }
        return null;  // return null if user is not found
    }

    public Users removeRoleFromUser(String username, Role role) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.getRoles().remove(role);
            return userRepository.save(user);
        }
        return null;  // return null if user is not found
    }

    public Users modifyUserRole(String username, Role newRole) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.getRoles().clear();  // Clear existing roles
            user.getRoles().add(newRole);  // Add the new role
            return userRepository.save(user);
        }
        return null;  // return null if user is not found
    }

    public boolean deleteUser(String username) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false; // User not found, cannot delete
        }

        Users user = userOpt.get();
        songService.deleteSongsByUser(user);
        commentService.deleteCommentsByUser(user);
        suggestionService.deleteSuggestionsByUser(user);

        userRepository.delete(user);
        return true; // User deleted successfully
    }

}