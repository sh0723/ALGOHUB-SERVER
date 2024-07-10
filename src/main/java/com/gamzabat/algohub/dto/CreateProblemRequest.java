package com.gamzabat.algohub.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateProblemRequest(@NotNull(message = "그룹 고유 아이디는 필수 입력 입니다.") Long id,
								   @NotBlank(message = "문제 링크는 필수 입력 입니다.") String link,
								   @NotNull(message = "마감 기한은 필수 입력 입니다.") LocalDate deadline) {
}
