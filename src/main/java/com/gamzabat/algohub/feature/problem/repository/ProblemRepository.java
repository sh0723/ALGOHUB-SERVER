package com.gamzabat.algohub.feature.problem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.feature.problem.domain.Problem;
import com.gamzabat.algohub.feature.studygroup.domain.StudyGroup;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
	List<Problem> findAllByStudyGroup(StudyGroup studyGroup);
	Problem getById(Long id);
}