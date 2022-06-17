package com.example.cheerik.repository;

import com.example.cheerik.model.Comment;
import com.example.cheerik.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
