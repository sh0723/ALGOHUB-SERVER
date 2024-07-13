package com.gamzabat.algohub.feature.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.feature.comment.domain.Comment;
import com.gamzabat.algohub.feature.solution.domain.Solution;

public interface CommentRepository extends JpaRepository<Comment,Long> {
	List<Comment> findAllBySolution(Solution solution);
}
