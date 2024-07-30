package com.gamzabat.algohub.feature.solution.service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import com.gamzabat.algohub.exception.StudyGroupValidationException;
import com.gamzabat.algohub.exception.UserValidationException;
import com.gamzabat.algohub.feature.solution.domain.Solution;
import com.gamzabat.algohub.feature.solution.dto.CreateSolutionRequest;
import com.gamzabat.algohub.feature.solution.dto.GetSolutionResponse;
import com.gamzabat.algohub.feature.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import com.gamzabat.algohub.feature.problem.domain.Problem;
import com.gamzabat.algohub.feature.studygroup.domain.StudyGroup;
import com.gamzabat.algohub.feature.studygroup.exception.GroupMemberValidationException;
import com.gamzabat.algohub.feature.studygroup.repository.GroupMemberRepository;
import com.gamzabat.algohub.feature.studygroup.repository.StudyGroupRepository;
import com.gamzabat.algohub.feature.user.domain.User;
import com.gamzabat.algohub.exception.ProblemValidationException;
import com.gamzabat.algohub.feature.problem.repository.ProblemRepository;
import com.gamzabat.algohub.feature.solution.repository.SolutionRepository;




import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SolutionService {
	private final SolutionRepository solutionRepository;
	private final ProblemRepository problemRepository;
	private final StudyGroupRepository studyGroupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final UserRepository userRepository;

	public List<GetSolutionResponse> getSolutionList(User user, Long problemId) {
		Problem problem = problemRepository.findById(problemId)
			.orElseThrow(() -> new ProblemValidationException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 문제 입니다."));

		StudyGroup group = studyGroupRepository.findById(problem.getStudyGroup().getId())
			.orElseThrow(() -> new StudyGroupValidationException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 그룹 입니다."));

		if(!groupMemberRepository.existsByUserAndStudyGroup(user, group)
			&& !group.getOwner().getId().equals(user.getId()))
			throw new GroupMemberValidationException(HttpStatus.FORBIDDEN.value(),"참여하지 않은 그룹 입니다.");

		List<Solution> solutions = solutionRepository.findAllByProblem(problem);
		log.info("success to get solution list");
		return solutions.stream().map(GetSolutionResponse::toDTO).toList();
	}

	public void createSolution(CreateSolutionRequest request) {

		List<Problem> problems = problemRepository.findAllByNumber(request.problemNumber());
		if (problems.isEmpty()) {
			throw new ProblemValidationException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 문제 입니다.");
		}

		User user = userRepository.findByBjNickname(request.userName())
				.orElseThrow(() -> new UserValidationException("존재하지 않는 유저 입니다."));

		Iterator<Problem> iterator = problems.iterator();

		while (iterator.hasNext()) {
			Problem problem = iterator.next();
			StudyGroup studyGroup = problem.getStudyGroup(); // problem에 딸린 그룹 고유id 로 studyGroup 가져오기

			LocalDate endDate = problem.getEndDate();
			LocalDate now = LocalDate.now();


			if (studyGroup.getOwner() != user || groupMemberRepository.existsByUserAndStudyGroup(user, studyGroup)
			||endDate == null || now.isAfter(endDate)) {
				iterator.remove();
				continue;
			}
			solutionRepository.save(Solution.builder()
					.problem(problem)
					.user(user)
					.content(request.code())
					.memoryUsage(request.memoryUsage())
					.executionTime(request.executionTime())
					.language(request.codeType())
					.codeLength(request.codeLength())
					.isCorrect(request.result().equals("맞았습니다!!"))
					.solvedDate(LocalDate.now())
					.build()
			);
		}

	}
	public void test(CreateSolutionRequest request) {
		log.info("username:"+request.userName());
		log.info("code:"+request.code());
	}


}
