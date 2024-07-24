package com.gamzabat.algohub.feature.solution.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;

import com.gamzabat.algohub.exception.StudyGroupValidationException;
import com.gamzabat.algohub.exception.UserValidationException;
import com.gamzabat.algohub.feature.comment.exception.SolutionValidationException;
import com.gamzabat.algohub.feature.solution.domain.Solution;
import com.gamzabat.algohub.feature.solution.dto.CreateSolutionRequest;
import com.gamzabat.algohub.feature.solution.dto.GetSolutionResponse;
import com.gamzabat.algohub.feature.user.repository.UserRepository;
import org.json.JSONObject;
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
		for(Problem problem : problems){
			StudyGroup studyGroup = studyGroupRepository.findById(problem.getStudyGroup());


		}


		JSONObject solutionInformation = getSolutionInformation(request.userName(), request.problemNumber(), request.submissionId());

		if (solutionInformation.isEmpty()) {
			throw new SolutionValidationException("해당 제출 기록이 없습니다.");
		}



		// 필드 값을 안전하게 변환
		int memoryUsage = parseIntegerSafely(solutionInformation.getString("memory"));
		int executionTime = parseIntegerSafely(solutionInformation.getString("time"));
		int codeLength = parseIntegerSafely(solutionInformation.getString("codeLength"));

		for(Problem problem : problems) {
			solutionRepository.save(Solution.builder()
					.problem(problem)
					.user(user)
					.content(request.code())
					.memoryUsage(memoryUsage)
					.executionTime(executionTime)
					.language(solutionInformation.getString("codeType"))
					.codeLength(codeLength)
					.isCorrect(solutionInformation.getString("result").equals("맞았습니다!!"))
					.solvedDate(LocalDate.now())
					.build()
			);
		}


	}
	public void test(CreateSolutionRequest request) {
		log.info("username:"+request.userName());
		log.info("code:"+request.code());
	}

    public JSONObject getSolutionInformation(@RequestParam String userName, @RequestParam Integer problemNumber, @RequestParam String submissionId) {
		try {
			// Python 스크립트 경로
			String scriptPath = "src/main/resources/crawlProblem.py";

			// ProcessBuilder를 사용하여 Python 스크립트 실행
			ProcessBuilder pb = new ProcessBuilder("python", scriptPath, userName, String.valueOf(problemNumber), submissionId);
			pb.redirectErrorStream(true);
			Process process = pb.start();

			// Python 스크립트의 출력을 읽어옵니다.
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder output = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line);
			}
			process.waitFor();

			String outputString = output.toString().trim();
			return new JSONObject(outputString);


		} catch (Exception e) {
			e.printStackTrace();
			// 예외 발생 시 빈 JSON 객체를 반환합니다.
			return new JSONObject();
		}
    }

	private int parseIntegerSafely(String str) {
		try {
			if (str == null || str.trim().isEmpty()) {
				return 0; // 기본값 설정 (또는 다른 적절한 기본값)
			}
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			// 변환 오류 발생 시 로그를 남기거나 기본값을 설정
			System.err.println("숫자 형식 오류: " + str);
			return 0; // 기본값 설정 (또는 다른 적절한 기본값)
		}
	}


}
