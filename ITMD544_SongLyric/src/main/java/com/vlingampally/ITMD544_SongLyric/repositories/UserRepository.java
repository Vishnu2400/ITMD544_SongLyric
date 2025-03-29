package com.vlingampally.ITMD544_SongLyric.repositories;

import com.vlingampally.ITMD544_SongLyric.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
}
