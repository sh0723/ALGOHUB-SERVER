package com.gamzabat.algohub.feature.solution.dto;

public record CreateSolutionRequest(String userId,
									String code,
									Integer problemNummber,
									String submissionId) {
}
