package com.gamzabat.algohub.repository.querydsl;

import java.util.List;

import com.gamzabat.algohub.domain.StudyGroup;
import com.gamzabat.algohub.domain.User;

public interface CustomStudyGroupRepository {
	List<StudyGroup> findByUser(User user);
}
