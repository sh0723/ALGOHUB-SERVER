package com.gamzabat.algohub.feature.solution.dto;

import java.time.LocalDate;

import com.gamzabat.algohub.feature.solution.domain.Solution;

import lombok.Builder;

@Builder
public record GetSolutionResponse(Long solutionId,
								  String nickname,
								  String profileImage,
								  LocalDate solvedDate,
								  String content,
								  boolean isCorrect,
								  Integer memoryUsage,
								  Integer executionTime,
								  String language,
								  Integer codeLength) {
	public static GetSolutionResponse toDTO(Solution solution){
		return GetSolutionResponse.builder()
			.solutionId(solution.getId())
			.nickname(solution.getUser().getNickname())
			.profileImage(solution.getUser().getProfileImage())
			.solvedDate(solution.getSolvedDate())
			.content(solution.getContent())
			.isCorrect(solution.isCorrect())
			.memoryUsage(solution.getMemoryUsage())
			.executionTime(solution.getExecutionTime())
			.language(solution.getLanguage())
			.codeLength(solution.getCodeLength())
			.build();
	}
}
