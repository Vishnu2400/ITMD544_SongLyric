package com.vlingampally.ITMD544_SongLyric.service;

import com.vlingampally.ITMD544_SongLyric.model.Role;
import com.vlingampally.ITMD544_SongLyric.model.Users;
import com.vlingampally.ITMD544_SongLyric.repositories.UserRepository;
import com.vlingampally.ITMD544_SongLyric.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public Map<String, String> register(String username, String email, String password, String roleStr) {
        if (userRepository.findByUsername(username).isPresent()) {
            return Map.of("message", "Username already taken");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return Map.of("message", "Email already in use");
        }

        Set<Role> roles = new HashSet<>();
        if (roleStr != null && !roleStr.isEmpty()) {
            try {
                roles.add(Role.valueOf(roleStr));
            } catch (IllegalArgumentException e) {
                return Map.of("message", "Invalid role provided");
            }
        } else {
            roles.add(Role.CONTRIBUTOR);
        }

        Users user = new Users();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);

        userRepository.save(user);

        return Map.of("message", "User registered successfully");
    }

    public Map<String, String> login(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return Map.of("token", token);
    }
}