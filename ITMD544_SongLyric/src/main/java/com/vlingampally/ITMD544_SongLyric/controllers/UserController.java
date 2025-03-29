package com.vlingampally.ITMD544_SongLyric.controllers;

import com.vlingampally.ITMD544_SongLyric.dto.UserDTO;
import com.vlingampally.ITMD544_SongLyric.model.Role;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import com.vlingampally.ITMD544_SongLyric.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Fetch a list of all users (for admins or authorized roles)
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Fetch a user by their username or email (for current authenticated user)
    @GetMapping("/me")
    public UserDTO getCurrentUser(@AuthenticationPrincipal User user) {
        Optional<Users> currentUser = userRepository.findByUsername(user.getUsername());
        return currentUser.map(this::convertToDTO).orElse(null);  // return the user or null if not found
    }

    // Fetch a user by their username (only accessible to authorized users)
    @GetMapping("/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        Optional<Users> user = userRepository.findByUsername(username);
        return user.map(this::convertToDTO).orElse(null);  // return the user or null if not found
    }

    // Update user details (e.g., password, email) for authenticated user
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateUserDetails(@AuthenticationPrincipal User authenticatedUser, @RequestBody Users updatedUser) {
        Optional<Users> userOpt = userRepository.findByUsername(authenticatedUser.getUsername());
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());  // password should be encrypted before updating
            userRepository.save(user);
            return ResponseEntity.ok(convertToDTO(user));
        }
        return ResponseEntity.notFound().build();  // return 404 if user is not found
    }

    // Add a role to a user (for admin or authorized roles)
    @PostMapping("/{username}/roles")
    public ResponseEntity<UserDTO> addRoleToUser(@PathVariable String username, @RequestBody Role role) {
        Optional<Users> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            Users existingUser = user.get();
            existingUser.getRoles().add(role);
            userRepository.save(existingUser);
            return ResponseEntity.ok(convertToDTO(existingUser));
        }
        return ResponseEntity.notFound().build();  // return 404 if user is not found
    }

    // Remove a role from a user (for admin or authorized roles)
    @DeleteMapping("/{username}/roles")
    public ResponseEntity<UserDTO> removeRoleFromUser(@PathVariable String username, @RequestBody Role role) {
        Optional<Users> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            Users existingUser = user.get();
            existingUser.getRoles().remove(role);
            userRepository.save(existingUser);
            return ResponseEntity.ok(convertToDTO(existingUser));
        }
        return ResponseEntity.notFound().build();  // return 404 if user is not found
    }

    // Modify a role of a user (for admin or authorized roles)
    @PutMapping("/{username}/roles")
    public ResponseEntity<UserDTO> modifyUserRole(@PathVariable String username, @RequestBody Role newRole) {
        Optional<Users> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            Users existingUser = user.get();
            existingUser.getRoles().clear();  // Clear existing roles
            existingUser.getRoles().add(newRole);  // Add the new role
            userRepository.save(existingUser);
            return ResponseEntity.ok(convertToDTO(existingUser));
        }
        return ResponseEntity.notFound().build();  // return 404 if user is not found
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        Optional<Users> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            userRepository.delete(user.get());  // delete user if found
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();  // return 404 if user is not found
    }

    // Convert Users entity to UserDTO
    private UserDTO convertToDTO(Users user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRoles(user.getRoles());
        return userDTO;
    }
}