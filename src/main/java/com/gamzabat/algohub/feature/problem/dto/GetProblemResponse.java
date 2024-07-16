package com.gamzabat.algohub.feature.problem.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class GetProblemResponse {
	private String title;
	private Long problemId;
	private String link;
	private LocalDate deadline;
	private Integer level;
	private Integer submitMemberCount;
	private Integer memberCount;
	private Integer accurancy;


	public GetProblemResponse(String title,Long problemId, String link, LocalDate deadline, Integer level, Integer submissionCount, Integer memberCount, Integer accurancy) {
		this.title = title;
		this.problemId = problemId;
		this.link = link;
		this.deadline = deadline;
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
