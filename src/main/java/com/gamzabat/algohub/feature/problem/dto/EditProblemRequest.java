package com.gamzabat.algohub.feature.problem.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EditProblemRequest(@NotNull(message = "문제 고유 아이디는 필수 입력 입니다.") Long problemId,
								 @NotNull(message = "시작 날짜는 필수 입력 입니다.") LocalDate startDate,
								 @NotNull(message = "마감 날짜는 필수 입력 입니다.") LocalDate endDate) {
}
