package com.gamzabat.algohub.feature.solution.service;

import java.util.List;

import com.gamzabat.algohub.feature.solution.domain.Solution;
import com.gamzabat.algohub.feature.solution.dto.GetSolutionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.gamzabat.algohub.feature.problem.domain.Problem;
import com.gamzabat.algohub.feature.user.domain.User;
import com.gamzabat.algohub.exception.ProblemValidationException;
import com.gamzabat.algohub.exception.UserValidationException;
import com.gamzabat.algohub.feature.problem.repository.ProblemRepository;
import com.gamzabat.algohub.feature.solution.repository.SolutionRepository;
import com.gamzabat.algohub.feature.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SolutionService {
	private final SolutionRepository solutionRepository;
	private final UserRepository userRepository;
	private final ProblemRepository problemRepository;
	public List<GetSolutionResponse> getSolutionList(Long userId, Long problemId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserValidationException("존재하지 않는 회원 입니다."));
		Problem problem = problemRepository.findById(problemId)
			.orElseThrow(() -> new ProblemValidationException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 문제 입니다."));
		List<Solution> solutions = solutionRepository.findAllByUserAndProblem(user, problem);
		log.info("success to get solution list");
		return solutions.stream().map(GetSolutionResponse::toDTO).toList();
	}
}
