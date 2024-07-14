package com.gamzabat.algohub.feature.problem.service;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamzabat.algohub.feature.problem.domain.Problem;
import com.gamzabat.algohub.feature.problem.dto.CreateProblemRequest;
import com.gamzabat.algohub.feature.problem.dto.EditProblemRequest;
import com.gamzabat.algohub.feature.problem.exception.NotBojLinkException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import com.gamzabat.algohub.feature.studygroup.domain.StudyGroup;
import com.gamzabat.algohub.feature.user.domain.User;
import com.gamzabat.algohub.feature.problem.dto.GetProblemResponse;
import com.gamzabat.algohub.exception.ProblemValidationException;
import com.gamzabat.algohub.exception.StudyGroupValidationException;
import com.gamzabat.algohub.feature.studygroup.repository.GroupMemberRepository;
import com.gamzabat.algohub.feature.problem.repository.ProblemRepository;
import com.gamzabat.algohub.feature.studygroup.repository.StudyGroupRepository;


import org.springframework.web.client.RestTemplate;

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

		int level = Integer.parseInt(getProblemLevel(getProblemId(request)));
		String title = getProblemTitle(getProblemId(request));

		problemRepository.save(Problem.builder()
			.studyGroup(group)
			.link(request.link())
				.title(title)
				.level(level)
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
	private String getProblemLevel(String problemId){
		final RestTemplate restTemplate= new RestTemplate();

		String url = "https://solved.ac/api/v3/problem/lookup?problemIds=" + problemId;

		try {
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
			String responseBody = responseEntity.getBody();

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(responseBody);
			if (root.isArray() && root.size() > 0) {
				JsonNode firstElement = root.get(0);
				int level = firstElement.get("level").asInt();
				return String.valueOf(level);
			} else {
				System.out.println("No data found for the given problem ID");
				return "No data found";
			}
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
			return "Error occurred";
		}
	}
	private String getProblemTitle(String problemId){
		final RestTemplate restTemplate= new RestTemplate();
		String url = "https://solved.ac/api/v3/problem/lookup?problemIds=" + problemId;

		try {
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
			String responseBody = responseEntity.getBody();

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(responseBody);
			if (root.isArray() && root.size() > 0) {
				JsonNode firstElement = root.get(0);
                return firstElement.get("titleKo").asText();
			} else {
				System.out.println("No data found for the given problem ID");
				return "No data found";
			}
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
			return "Error occurred";
		}
	}
	private String getProblemId(CreateProblemRequest reuqest) {
		String url = reuqest.link();
		String[] parts = url.split("/");
		if(!parts[2].equals("www.acmicpc.net"))
			throw new NotBojLinkException(HttpStatus.BAD_REQUEST.value(),"백준 링크가 아닙니다");
        return parts[parts.length - 1];
	}
}
