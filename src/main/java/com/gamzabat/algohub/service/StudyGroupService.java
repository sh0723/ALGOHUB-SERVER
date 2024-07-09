package com.gamzabat.algohub.service;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.gamzabat.algohub.domain.GroupMember;
import com.gamzabat.algohub.domain.StudyGroup;
import com.gamzabat.algohub.domain.User;
import com.gamzabat.algohub.exception.GroupMemberValidationException;
import com.gamzabat.algohub.exception.StudyGroupValidationException;
import com.gamzabat.algohub.repository.GroupMemberRepository;
import com.gamzabat.algohub.repository.StudyGroupRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudyGroupService {
	private final StudyGroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final ImageService imageService;

	public void createGroup(User user, String name, MultipartFile profileImage) {
		String imageUrl = imageService.saveImage(profileImage);
		groupRepository.save(StudyGroup.builder()
			.name(name)
			.groupImage(imageUrl)
			.owner(user)
			.groupCode(NanoIdUtils.randomNanoId())
			.build());
		log.info("success to save study group");
	}

	public void joinGroupWithCode(User user, String code) {
		StudyGroup studyGroup = groupRepository.findByGroupCode(code)
			.orElseThrow(() -> new StudyGroupValidationException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 그룹 입니다."));

		if(groupMemberRepository.existsByUserAndStudyGroup(user, studyGroup)
			|| studyGroup.getOwner().getId().equals(user.getId()))
			throw new StudyGroupValidationException(HttpStatus.BAD_REQUEST.value(),"이미 참여한 그룹 입니다.");

		groupMemberRepository.save(
			GroupMember.builder()
				.studyGroup(studyGroup)
				.user(user)
				.joinDate(LocalDate.now())
				.build()
		);
		log.info("success to join study group");
	}

	public void deleteGroup(User user, Long groupId) {
		StudyGroup studyGroup = groupRepository.findById(groupId)
			.orElseThrow(() -> new StudyGroupValidationException(HttpStatus.NOT_FOUND.value(),"존재하지 않는 그룹 입니다."));

		if(studyGroup.getOwner().getId().equals(user.getId())){ // owner
			groupRepository.delete(studyGroup);
		}
		else{ // member
			GroupMember member = groupMemberRepository.findByUserAndStudyGroup(user, studyGroup)
				.orElseThrow(() -> new GroupMemberValidationException(HttpStatus.BAD_REQUEST.value(),"이미 참여하지 않은 그룹 입니다."));
			groupMemberRepository.delete(member);
		}
		log.info("success to delete(exit) study group");
	}
}
