package com.gamzabat.algohub.feature.problem.repository;

import java.util.List;
import java.util.Optional;

import com.gamzabat.algohub.feature.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.feature.problem.domain.Problem;
import com.gamzabat.algohub.feature.studygroup.domain.StudyGroup;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
	Page<Problem> findAllByStudyGroup(StudyGroup studyGroup, Pageable pageable);
	Problem getById(Long id);
	List<Problem> findAllByNumber(Integer Number);

}
