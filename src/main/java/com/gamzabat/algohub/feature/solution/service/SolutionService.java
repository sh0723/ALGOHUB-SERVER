package com.gamzabat.algohub.feature.solution.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import com.gamzabat.algohub.exception.StudyGroupValidationException;
import com.gamzabat.algohub.exception.UserValidationException;
import com.gamzabat.algohub.feature.comment.exception.SolutionValidationException;
import com.gamzabat.algohub.feature.solution.domain.Solution;
import com.gamzabat.algohub.feature.solution.dto.CreateSolutionRequest;
import com.gamzabat.algohub.feature.solution.dto.GetSolutionResponse;
import com.gamzabat.algohub.feature.user.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;

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

	public Page<GetSolutionResponse> getSolutionList(User user, Long problemId, Pageable pageable) {
		Problem problem = problemRepository.findById(problemId)
				.orElseThrow(() -> new ProblemValidationException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 문제 입니다."));

		StudyGroup group = studyGroupRepository.findById(problem.getStudyGroup().getId())
				.orElseThrow(() -> new StudyGroupValidationException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 그룹 입니다."));

		if(!groupMemberRepository.existsByUserAndStudyGroup(user, group)
				&& !group.getOwner().getId().equals(user.getId())) {
			throw new GroupMemberValidationException(HttpStatus.FORBIDDEN.value(),"참여하지 않은 그룹 입니다.");
		}

		Page<Solution> solutions = solutionRepository.findAllByProblem(problem, pageable);
		log.info("success to get solution list");

		return solutions.map(GetSolutionResponse::toDTO);
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
			if (studyGroup.getOwner() != user || groupMemberRepository.existsByUserAndStudyGroup(user, studyGroup)) {
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
					.isCorrect(request.result().equals("맞았습니다!!")) // assuming "정답" means correct
					.solvedDate(LocalDate.now())
					.build()
			);
		}

	}
	public void test(CreateSolutionRequest request) {
		log.info("username:"+request.userName());
		log.info("code:"+request.code());
	}

//    public JSONObject getSolutionInformation(@RequestParam String userName, @RequestParam Integer problemNumber, @RequestParam String submissionId) {
//		try {
//
//			// Python 스크립트 경로
//			String scriptPath = "src/main/resources/crawlProblem.py";
//
//			// ProcessBuilder를 사용하여 Python 스크립트 실행
//			ProcessBuilder pb = new ProcessBuilder("python", scriptPath, userName, String.valueOf(problemNumber), submissionId);
//			pb.redirectErrorStream(true);
//			Process process = pb.start();
//
//			// Python 스크립트의 출력을 읽어옵니다.
//			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
//			StringBuilder output = new StringBuilder();
//			String line;
//
//			while ((line = reader.readLine()) != null) {
//				output.append(line);
//			}
//			process.waitFor();
//
//			// JSON 형식의 문자열을 반환합니다.
//			String outputString = output.toString().trim();
//
//			// JSON 문자열을 로그로 출력하여 확인합니다.
//			System.out.println("Output String: " + outputString);
//
//
//			return new JSONObject(outputString);
//
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			// 예외 발생 시 빈 JSON 객체를 반환합니다.
//			return new JSONObject();
//		}
//    }
//
//	private int parseIntegerSafely(String str) {
//		try {
//			if (str == null || str.trim().isEmpty()) {
//				return 0; // 기본값 설정
//			}
//			return Integer.parseInt(str);
//		} catch (NumberFormatException e) {
//			return 0; // 기본값 설정
//		}
//	}
	private boolean parseBooleanSafely(String result){
		return "맞았습니다!!".equals(result.trim());

	}

}
