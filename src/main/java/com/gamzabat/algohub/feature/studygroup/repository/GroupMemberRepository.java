package com.gamzabat.algohub.feature.studygroup.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.feature.studygroup.domain.GroupMember;
import com.gamzabat.algohub.feature.studygroup.domain.StudyGroup;
import com.gamzabat.algohub.feature.user.domain.User;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
	boolean existsByUserAndStudyGroup(User user, StudyGroup studyGroup);
	Optional<GroupMember> findByUserAndStudyGroup(User user, StudyGroup studyGroup);
	List<GroupMember> findAllByStudyGroup(StudyGroup studyGroup);

}