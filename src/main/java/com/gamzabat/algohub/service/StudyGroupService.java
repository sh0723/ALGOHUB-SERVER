package com.gamzabat.algohub.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.gamzabat.algohub.domain.StudyGroup;
import com.gamzabat.algohub.domain.User;
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
}
