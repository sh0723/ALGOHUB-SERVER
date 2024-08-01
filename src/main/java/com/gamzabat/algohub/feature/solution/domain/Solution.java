package com.gamzabat.algohub.feature.solution.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.gamzabat.algohub.feature.user.domain.User;
import com.gamzabat.algohub.feature.problem.domain.Problem;
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
	private LocalDateTime solvedDateTime;
	private String content;
	private boolean isCorrect;
	private Integer memoryUsage;
	private Integer executionTime;
	private String language;
	private Integer codeLength;

	@Builder
	public Solution(Problem problem, User user, LocalDate solvedDate,LocalDateTime solvedDateTime, String content, boolean isCorrect,
					Integer memoryUsage, Integer executionTime, String language, Integer codeLength) {
		this.problem = problem;
		this.user = user;
		this.solvedDate = solvedDate;
		this.solvedDateTime = solvedDateTime;        //??
		this.content = content;                //??
		this.isCorrect = isCorrect;            //정답 여부
		this.memoryUsage = memoryUsage;    //메모리
		this.executionTime = executionTime; //시간
		this.language = language;            //사용 언어
		this.codeLength = codeLength;        //코드 길이
	}

	public LocalDateTime getSolvedDateTime() {
		return solvedDateTime;
	}
}
