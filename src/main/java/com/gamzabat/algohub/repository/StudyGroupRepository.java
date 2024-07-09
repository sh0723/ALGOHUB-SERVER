package com.gamzabat.algohub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.domain.StudyGroup;
import com.gamzabat.algohub.repository.querydsl.CustomStudyGroupRepository;

public interface StudyGroupRepository extends JpaRepository<StudyGroup,Long>, CustomStudyGroupRepository {
	Optional<StudyGroup> findByGroupCode(String groupCode);
}
