package com.vlingampally.ITMD544_SongLyric.repositories;

import com.vlingampally.ITMD544_SongLyric.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllBySongId(Long songId);
}