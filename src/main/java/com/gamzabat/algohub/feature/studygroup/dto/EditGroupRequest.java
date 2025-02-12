package com.gamzabat.algohub.feature.studygroup.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record EditGroupRequest(@NotNull(message = "그룹 고유 아이디는 필수 입력 입니다.") Long id,
							   String name,
							   LocalDate startDate,
							   LocalDate endDate,
							   String introduction) {
}
