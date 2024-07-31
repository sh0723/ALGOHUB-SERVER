package com.gamzabat.algohub.feature.solution.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.gamzabat.algohub.feature.solution.domain.Solution;

import lombok.Builder;

@Builder
public record GetSolutionResponse(Long solutionId,
								  String nickname,
								  String profileImage,
								  LocalDateTime solvedDateTime,
								  String content,
								  boolean isCorrect,
								  Integer memoryUsage,
								  Integer executionTime,
								  String language,
								  Integer codeLength,
								  Long commentCount) {
	public static GetSolutionResponse toDTO(Solution solution, Long commentCount){
		return GetSolutionResponse.builder()
			.solutionId(solution.getId())
			.nickname(solution.getUser().getNickname())
			.profileImage(solution.getUser().getProfileImage())
			.solvedDateTime(solution.getSolvedDateTime())
			.content(solution.getContent())
			.isCorrect(solution.isCorrect())
			.memoryUsage(solution.getMemoryUsage())
			.executionTime(solution.getExecutionTime())
			.language(solution.getLanguage())
			.codeLength(solution.getCodeLength())
			.commentCount(commentCount)
			.build();
	}
}
