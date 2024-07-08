package com.gamzabat.algohub.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Solution {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "problem_id")
	private Problem problem;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private LocalDate solvedDate;
	private String content;
	private Integer memoryUsage;
	private Integer executionTime;

	@Builder
	public Solution(Problem problem, User user, LocalDate solvedDate, String content, Integer memoryUsage,
		Integer executionTime) {
		this.problem = problem;
		this.user = user;
		this.solvedDate = solvedDate;
		this.content = content;
		this.memoryUsage = memoryUsage;
		this.executionTime = executionTime;
	}
}
