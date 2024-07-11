package com.gamzabat.algohub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
