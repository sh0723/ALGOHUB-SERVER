package com.gamzabat.algohub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.domain.Problem;
import com.gamzabat.algohub.domain.Solution;
import com.gamzabat.algohub.domain.User;

public interface SolutionRepository extends JpaRepository<Solution,Long> {
	List<Solution> findAllByUserAndProblem(User user, Problem problem);
	Solution findByUserAndProblem(User user, Problem problem);
}
