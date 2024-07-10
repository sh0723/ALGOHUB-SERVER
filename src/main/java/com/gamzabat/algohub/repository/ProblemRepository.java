package com.gamzabat.algohub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.domain.Problem;
import com.gamzabat.algohub.domain.StudyGroup;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
	List<Problem> findAllByStudyGroup(StudyGroup studyGroup);
}
