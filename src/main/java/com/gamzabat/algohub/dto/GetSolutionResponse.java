package com.gamzabat.algohub.dto;

import java.time.LocalDate;

import com.gamzabat.algohub.domain.Solution;

import lombok.Builder;

@Builder
public record GetSolutionResponse(Long solutionId,
								  LocalDate solvedDate,
								  String content,
								  Integer memoryUsage,
								  Integer executionTime) {
	public static GetSolutionResponse toDTO(Solution solution){
		return GetSolutionResponse.builder()
			.solutionId(solution.getId())
			.solvedDate(solution.getSolvedDate())
			.content(solution.getContent())
			.memoryUsage(solution.getMemoryUsage())
			.executionTime(solution.getExecutionTime())
			.build();
	}
}
