package com.gamzabat.algohub.feature.problem.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class GetProblemResponse {
	private String title;
	private Long problemId;
	private String link;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer level;
	private Integer submitMemberCount;
	private Integer memberCount;
	private Integer accurancy;


	public GetProblemResponse(String title,Long problemId, String link, LocalDate startDate, LocalDate endDate, Integer level, Integer submissionCount, Integer memberCount, Integer accurancy) {
		this.title = title;
		this.problemId = problemId;
		this.link = link;
		this.startDate = startDate;
		this.endDate = endDate;
		this.level = level;
		this.submitMemberCount = submissionCount;
		this.memberCount = memberCount;
		this.accurancy = accurancy;
	}
}
/*public record GetProblemResponse(Long problemId,
								 String link,
								 String title,
								 LocalDate deadline,
								 Integer level,
								 Integer submissionCount,
								 Integer memberCount,
								 Integer accurancy) {
	public static GetProblemResponse toDTO(Problem problem){
		return GetProblemResponse.builder()
			.problemId(problem.getId())
			.link(problem.getLink())
			.title(problem.getTitle())
			.deadline(problem.getDeadline())
			.level(problem.getLevel())
			.build();
	}
}*/
