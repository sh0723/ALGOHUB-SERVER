package com.gamzabat.algohub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamzabat.algohub.dto.CreateSolutionRequest;
import com.gamzabat.algohub.dto.GetSolutionResponse;
import com.gamzabat.algohub.service.SolutionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/solution")
@Tag(name = "풀이 API", description = "문제 풀이 관련 API")
public class SolutionController {
	private final SolutionService solutionService;

	@GetMapping
	@Operation(summary = "풀이 조회 API", description = "유저 한 명의 특정 문제 풀이를 모두 조회하는 API")
	public ResponseEntity<List<GetSolutionResponse>> getSolutionList(
		@RequestParam Long problemId, @RequestParam Long userId){
		List<GetSolutionResponse> response = solutionService.getSolutionList(userId,problemId);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping
	@Operation(summary = "(사용X : extension 테스트 API)")
	public ResponseEntity<Object> test(@RequestBody CreateSolutionRequest request){
		solutionService.test(request);
		return ResponseEntity.ok().body("OK");
	}

}
