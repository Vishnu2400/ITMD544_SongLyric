package com.vlingampally.ITMD544_SongLyric.controllers;

import com.vlingampally.ITMD544_SongLyric.dto.UserDTO;
import com.vlingampally.ITMD544_SongLyric.model.Role;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import com.vlingampally.ITMD544_SongLyric.repositories.UserRepository;
import com.vlingampally.ITMD544_SongLyric.service.UserService;
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
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // Fetch a list of all users (for admins or authorized roles)
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Fetch a user by their username or email (for current authenticated user)
    @GetMapping("/me")
    public UserDTO getCurrentUser(@AuthenticationPrincipal User user) {
        Optional<Users> currentUser = userService.getCurrentUser(user.getUsername());
        return currentUser.map(this::convertToDTO).orElse(null);  // return the user or null if not found
    }

    // Fetch a user by their username (only accessible to authorized users)
    @GetMapping("/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        Optional<Users> user = userService.getCurrentUser(username);
        return user.map(this::convertToDTO).orElse(null);  // return the user or null if not found
    }

    // Update user details (e.g., password, email) for authenticated user
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateUserDetails(@AuthenticationPrincipal User authenticatedUser, @RequestBody Users updatedUser) {
        Users user = userService.updateUserDetails(authenticatedUser.getUsername(), updatedUser);
        if (user != null) {
            return ResponseEntity.ok(convertToDTO(user));
        }
        return ResponseEntity.notFound().build();  // return 404 if user is not found
    }

    // Update user name for authenticated user
    @PutMapping("/me/name")
    public ResponseEntity<UserDTO> updateUserName(@AuthenticationPrincipal User authenticatedUser, @RequestBody String newName) {
        Users user = userService.updateUserName(authenticatedUser.getUsername(), newName);
        if (user != null) {
            return ResponseEntity.ok(convertToDTO(user));
        }
        return ResponseEntity.notFound().build();  // return 404 if user is not found
    }

    // Update user email for authenticated user
    @PutMapping("/me/email")
    public ResponseEntity<UserDTO> updateUserEmail(@AuthenticationPrincipal User authenticatedUser, @RequestBody String newEmail) {
        Users user = userService.updateUserEmail(authenticatedUser.getUsername(), newEmail);
        if (user != null) {
            return ResponseEntity.ok(convertToDTO(user));
        }
        return ResponseEntity.notFound().build();  // return 404 if user is not found
    }

    // Update user password for authenticated user
    @PutMapping("/me/password")
    public ResponseEntity<UserDTO> updateUserPassword(@AuthenticationPrincipal User authenticatedUser, @RequestBody String newPassword) {
        Users user = userService.updateUserPassword(authenticatedUser.getUsername(), newPassword);
        if (user != null) {
            return ResponseEntity.ok(convertToDTO(user));
        }
        return ResponseEntity.notFound().build();  // return 404 if user is not found
    }

    // Add a role to current user
    @PostMapping("/me/roles")
    public ResponseEntity<UserDTO> addRoleToUser(@AuthenticationPrincipal User authenticatedUser, @RequestBody Role role) {
        Users user = userService.addRoleToUser(authenticatedUser.getUsername(), role);
        if (user != null) {
            return ResponseEntity.ok(convertToDTO(user));
        }
        return ResponseEntity.notFound().build();
    }

    // Remove a role from a user (for admin or authorized roles)
    @DeleteMapping("/me/roles")
    public ResponseEntity<UserDTO> removeRoleFromUser(@AuthenticationPrincipal User authenticatedUser, @RequestBody Role role) {
        Users user = userService.removeRoleFromUser(authenticatedUser.getUsername(), role);
        if (user != null) {
            return ResponseEntity.ok(convertToDTO(user));
        }
        return ResponseEntity.notFound().build();
    }

    // Modify a role of a user (for admin or authorized roles)
    @PutMapping("/me/roles")
    public ResponseEntity<UserDTO> modifyUserRole(@AuthenticationPrincipal User authenticatedUser, @RequestBody Role newRole) {
        Users user = userService.modifyUserRole(authenticatedUser.getUsername(), newRole);
        if (user != null) {
            return ResponseEntity.ok(convertToDTO(user));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal User authenticatedUser) {
        boolean isDeleted = userService.deleteUser(authenticatedUser.getUsername());
        if (isDeleted) {
            return ResponseEntity.ok().build();
        }
        else
            return ResponseEntity.notFound().build();
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