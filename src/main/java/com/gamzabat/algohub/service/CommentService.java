package com.gamzabat.algohub.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.gamzabat.algohub.domain.Comment;
import com.gamzabat.algohub.domain.Problem;
import com.gamzabat.algohub.domain.Solution;
import com.gamzabat.algohub.domain.StudyGroup;
import com.gamzabat.algohub.domain.User;
import com.gamzabat.algohub.dto.CreateCommentRequest;
import com.gamzabat.algohub.dto.GetCommentResponse;
import com.gamzabat.algohub.exception.GroupMemberValidationException;
import com.gamzabat.algohub.exception.ProblemValidationException;
import com.gamzabat.algohub.exception.SolutionValidationException;
import com.gamzabat.algohub.exception.StudyGroupValidationException;
import com.gamzabat.algohub.repository.CommentRepository;
import com.gamzabat.algohub.repository.GroupMemberRepository;
import com.gamzabat.algohub.repository.ProblemRepository;
import com.gamzabat.algohub.repository.SolutionRepository;
import com.gamzabat.algohub.repository.StudyGroupRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final SolutionRepository solutionRepository;
	private final ProblemRepository problemRepository;
	private final StudyGroupRepository studyGroupRepository;
	private final GroupMemberRepository groupMemberRepository;
	public void createComment(User user, CreateCommentRequest request) {
		Solution solution = checkSolutionValidation(user, request.solutionId());

		commentRepository.save(Comment.builder()
				.user(user)
				.solution(solution)
				.content(request.content())
				.createdAt(LocalDateTime.now())
			.build());
		log.info("success to create comment");
	}

	public List<GetCommentResponse> getCommentList(User user, Long solutionId) {
		Solution solution = checkSolutionValidation(user, solutionId);
		List<Comment> list = commentRepository.findAllBySolution(solution);
		List<GetCommentResponse> result = list.stream().map(GetCommentResponse::toDTO).toList();
		log.info("success to get comment list");
		return result;
	}

	private Solution checkSolutionValidation(User user, Long solutionId) {
		Solution solution = solutionRepository.findById(solutionId)
			.orElseThrow(() -> new SolutionValidationException("존재하지 않는 풀이 입니다."));

		Problem problem = problemRepository.findById(solution.getProblem().getId())
			.orElseThrow(() -> new ProblemValidationException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 문제 입니다."));

		StudyGroup group = studyGroupRepository.findById(problem.getStudyGroup().getId())
			.orElseThrow(() -> new StudyGroupValidationException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 그룹 입니다."));

		if(!groupMemberRepository.existsByUserAndStudyGroup(user, group)
			&& !group.getOwner().getId().equals(user.getId()))
			throw new GroupMemberValidationException(HttpStatus.FORBIDDEN.value(),"참여하지 않은 그룹 입니다.");

		return solution;
	}
}
