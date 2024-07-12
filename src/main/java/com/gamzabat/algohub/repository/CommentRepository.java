package com.gamzabat.algohub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.domain.Comment;
import com.gamzabat.algohub.domain.Solution;

public interface CommentRepository extends JpaRepository<Comment,Long> {
	List<Comment> findAllBySolution(Solution solution);
}
