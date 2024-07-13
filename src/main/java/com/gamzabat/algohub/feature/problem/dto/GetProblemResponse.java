package com.gamzabat.algohub.feature.problem.dto;

import java.time.LocalDate;

import com.gamzabat.algohub.feature.problem.domain.Problem;

import lombok.Builder;

@Builder
public record GetProblemResponse(Long problemId,
								 String link,
								 String title,
								 LocalDate deadline,
								 Integer level) {
	public static GetProblemResponse toDTO(Problem problem){
		return GetProblemResponse.builder()
			.problemId(problem.getId())
			.link(problem.getLink())
			.title(problem.getTitle())
			.deadline(problem.getDeadline())
			.level(problem.getLevel())
			.build();
	}
}
