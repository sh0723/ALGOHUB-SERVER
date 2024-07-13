package com.gamzabat.algohub.feature.problem.service;

import java.util.List;

import com.gamzabat.algohub.feature.problem.domain.Problem;
import com.gamzabat.algohub.feature.problem.dto.CreateProblemRequest;
import com.gamzabat.algohub.feature.problem.dto.EditProblemRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.gamzabat.algohub.feature.studygroup.domain.StudyGroup;
import com.gamzabat.algohub.feature.user.domain.User;
import com.gamzabat.algohub.feature.problem.dto.GetProblemResponse;
import com.gamzabat.algohub.exception.ProblemValidationException;
import com.gamzabat.algohub.exception.StudyGroupValidationException;
import com.gamzabat.algohub.feature.studygroup.repository.GroupMemberRepository;
import com.gamzabat.algohub.feature.problem.repository.ProblemRepository;
import com.gamzabat.algohub.feature.studygroup.repository.StudyGroupRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProblemService {
	private final ProblemRepository problemRepository;
	private final StudyGroupRepository studyGroupRepository;
	private final GroupMemberRepository groupMemberRepository;
	public void createProblem(User user, CreateProblemRequest request) {
		StudyGroup group = getGroup(request.groupId());

		checkOwnerPermission(user, group, "create");

		problemRepository.save(Problem.builder() // 크롤링 후 level, title 정보 필요
			.studyGroup(group)
			.link(request.link())
			.deadline(request.deadline())
			.build());

		log.info("success to create problem");
	}

	public void editProblem(User user, EditProblemRequest request) {
		Problem problem = getProblem(request.problemId());
		StudyGroup group = getGroup(problem.getStudyGroup().getId());
		checkOwnerPermission(user, group, "edit");

		problem.editDeadline(request.deadline());
		log.info("success to edit problem deadline");
	}

	public List<GetProblemResponse> getProblemList(User user, Long groupId) {
		StudyGroup group = getGroup(groupId);
		if(!group.getOwner().getId().equals(user.getId())
			&&!groupMemberRepository.existsByUserAndStudyGroup(user,group))
			throw new ProblemValidationException(HttpStatus.FORBIDDEN.value(),"문제를 조회할 권한이 없습니다.");

		List<Problem> problems = problemRepository.findAllByStudyGroup(group);
		List<GetProblemResponse> list = problems.stream().map(GetProblemResponse::toDTO).toList();
		log.info("success to get problem list");
		return list;
	}

	public void deleteProblem(User user, Long problemId) {
		Problem problem = getProblem(problemId);
		StudyGroup group = getGroup(problem.getStudyGroup().getId());
		checkOwnerPermission(user, group, "delete");

		problemRepository.delete(problem);
		log.info("success to delete problem");
	}

	private Problem getProblem(Long problemId) {
		return problemRepository.findById(problemId)
			.orElseThrow(() -> new ProblemValidationException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 문제 입니다."));
	}

	private StudyGroup getGroup(Long id) {
		return studyGroupRepository.findById(id)
			.orElseThrow(() -> new StudyGroupValidationException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 그룹 입니다."));
	}

	private static void checkOwnerPermission(User user, StudyGroup group, String permission) {
		if(!group.getOwner().getId().equals(user.getId()))
			throw new StudyGroupValidationException(HttpStatus.FORBIDDEN.value(), "문제에 대한 권한이 없습니다. : "+permission);
	}
}
