package com.gamzabat.algohub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.domain.StudyGroup;

public interface StudyGroupRepository extends JpaRepository<StudyGroup,Long> {
}
