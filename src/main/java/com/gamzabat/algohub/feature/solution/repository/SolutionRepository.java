package com.gamzabat.algohub.feature.solution.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.feature.problem.domain.Problem;
import com.gamzabat.algohub.feature.solution.domain.Solution;
import com.gamzabat.algohub.feature.user.domain.User;

public interface SolutionRepository extends JpaRepository<Solution,Long> {
	List<Solution> findAllByProblem(Problem problem);
	Boolean existsByUserAndProblem(User user, Problem problem);
}
