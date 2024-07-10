package com.gamzabat.algohub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamzabat.algohub.common.annotation.AuthedUser;
import com.gamzabat.algohub.domain.User;
import com.gamzabat.algohub.dto.CreateProblemRequest;
import com.gamzabat.algohub.dto.EditProblemRequest;
import com.gamzabat.algohub.dto.GetProblemResponse;
import com.gamzabat.algohub.exception.RequestException;
import com.gamzabat.algohub.service.ProblemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problem")
@Tag(name = "문제 API", description = "그룹별 문제 관련 API")
public class ProblemController {
	private final ProblemService problemService;

	@PostMapping
	@Operation(summary = "문제 생성 API")
	public ResponseEntity<Object> createProblem(@AuthedUser User user,
		@Valid @RequestBody CreateProblemRequest request, Errors errors){
		if(errors.hasErrors())
			throw new RequestException("문제 생성 요청이 올바르지 않습니다.",errors);
		problemService.createProblem(user, request);
		return ResponseEntity.ok().body("OK");
	}

	@PatchMapping
	@Operation(summary = "문제 마감 기한 수정 API")
	public ResponseEntity<Object> editProblemDeadline(@AuthedUser User user,
		@Valid @RequestBody EditProblemRequest request, Errors errors){
		if(errors.hasErrors())
			throw new RequestException("문제 마감 기한 수정 요청이 올바르지 않습니다.",errors);
		problemService.editProblem(user, request);
		return ResponseEntity.ok().body("OK");
	}

	@GetMapping
	@Operation(summary = "문제 목록 조회 API", description = "그룹 하나에 대해 모든 문제를 조회하는 API")
	public ResponseEntity<List<GetProblemResponse>> getProblemList(@AuthedUser User user, @RequestParam Long groupId){
		List<GetProblemResponse> response = problemService.getProblemList(user, groupId);
		return ResponseEntity.ok().body(response);
	}

}
