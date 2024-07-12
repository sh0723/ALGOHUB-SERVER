package com.gamzabat.algohub.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.gamzabat.algohub.domain.Problem;
import com.gamzabat.algohub.domain.Solution;
import com.gamzabat.algohub.domain.User;
import com.gamzabat.algohub.dto.GetSolutionResponse;
import com.gamzabat.algohub.exception.ProblemValidationException;
import com.gamzabat.algohub.exception.UserValidationException;
import com.gamzabat.algohub.repository.ProblemRepository;
import com.gamzabat.algohub.repository.SolutionRepository;
import com.gamzabat.algohub.repository.UserRepository;

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
	public List<GetSolutionResponse> getSolutionList(Long userId,Long problemId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserValidationException("존재하지 않는 회원 입니다."));
		Problem problem = problemRepository.findById(problemId)
			.orElseThrow(() -> new ProblemValidationException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 문제 입니다."));
		List<Solution> solutions = solutionRepository.findAllByUserAndProblem(user, problem);
		log.info("success to get solution list");
		return solutions.stream().map(GetSolutionResponse::toDTO).toList();
	}
}
