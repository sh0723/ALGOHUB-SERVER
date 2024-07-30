package com.gamzabat.algohub.feature.solution.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.feature.problem.domain.Problem;
import com.gamzabat.algohub.feature.solution.domain.Solution;
import com.gamzabat.algohub.feature.user.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SolutionRepository extends JpaRepository<Solution,Long> {
	Page<Solution> findAllByProblem(Problem problem, Pageable pageable);
	Boolean existsByUserAndProblem(User user, Problem problem);

	@Query("SELECT COUNT(DISTINCT s.user) FROM Solution s WHERE s.problem.id = :problemId")
	Integer countDistinctUsersByProblemId(@Param("problemId") Long problemId);

	@Query("SELECT COUNT(DISTINCT s.user) FROM Solution s WHERE s.problem.id = :problemId AND s.isCorrect = true")
	Integer countDistinctUsersWithCorrectSolutionsByProblemId(@Param("problemId") Long problemId);

}
