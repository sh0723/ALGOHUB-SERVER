package com.gamzabat.algohub.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gamzabat.algohub.common.annotation.AuthedUser;
import com.gamzabat.algohub.domain.User;
import com.gamzabat.algohub.service.StudyGroupService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
@Tag(name = "그룹 API", description = "스터디 그룹 관련 API")
public class StudyGroupController {
	private final StudyGroupService studyGroupService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "그룹 생성 API")
	public ResponseEntity<Object> createGroup(@AuthedUser User user,
		@RequestParam String name, @RequestPart(required = false) MultipartFile profileImage){
		studyGroupService.createGroup(user, name, profileImage);
		return ResponseEntity.ok().body("OK");
	}

}
