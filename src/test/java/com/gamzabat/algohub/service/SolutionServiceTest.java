package com.gamzabat.algohub.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import com.gamzabat.algohub.exception.StudyGroupValidationException;
import com.gamzabat.algohub.feature.solution.service.SolutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.gamzabat.algohub.feature.problem.domain.Problem;
import com.gamzabat.algohub.feature.solution.domain.Solution;
import com.gamzabat.algohub.feature.studygroup.domain.StudyGroup;
import com.gamzabat.algohub.feature.studygroup.exception.GroupMemberValidationException;
import com.gamzabat.algohub.feature.studygroup.repository.GroupMemberRepository;
import com.gamzabat.algohub.feature.studygroup.repository.StudyGroupRepository;
import com.gamzabat.algohub.feature.user.domain.User;
import com.gamzabat.algohub.feature.solution.dto.GetSolutionResponse;
import com.gamzabat.algohub.enums.Role;
import com.gamzabat.algohub.exception.ProblemValidationException;
import com.gamzabat.algohub.feature.problem.repository.ProblemRepository;
import com.gamzabat.algohub.feature.solution.repository.SolutionRepository;

@ExtendWith(MockitoExtension.class)
class SolutionServiceTest {
	@InjectMocks
	private SolutionService solutionService;
	@Mock
	private SolutionRepository solutionRepository;
	@Mock
	private ProblemRepository problemRepository;
	@Mock
	private StudyGroupRepository studyGroupRepository;
	@Mock
	private GroupMemberRepository groupMemberRepository;
	private User user,user2;
	private Problem problem;
	private StudyGroup group;
	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		user = User.builder().email("email1").password("password").nickname("nickname")
			.role(Role.USER).profileImage("profileImage").build();
		user2 = User.builder().email("email2").password("password").nickname("nickname")
			.role(Role.USER).profileImage("profileImage").build();
		group = StudyGroup.builder().name("name").owner(user).groupImage("imageUrl").groupCode("code").build();
		problem = Problem.builder().studyGroup(group).link("link").deadline(LocalDate.now()).build();

		Field userField = User.class.getDeclaredField("id");
		userField.setAccessible(true);
		userField.set(user,1L);
		userField.set(user2,2L);

		Field problemField = Problem.class.getDeclaredField("id");
		problemField.setAccessible(true);
		problemField.set(problem,10L);

		Field groupField = StudyGroup.class.getDeclaredField("id");
		groupField.setAccessible(true);
		groupField.set(group,30L);
	}

	@Test
	@DisplayName("풀이 목록 조회 성공 (주인)")
	void getSolutionList() {
		// given
		List<Solution> list = new ArrayList<>(30);
		for(int i=0; i<30; i++) {
			list.add(Solution.builder()
				.problem(problem)
					.content("content"+i)
					.user(user)
					.memoryUsage(i)
					.executionTime(i)
					.isCorrect(true)
					.language("Java"+i)
					.codeLength(i)
				.build());
		}
		when(studyGroupRepository.findById(30L)).thenReturn(Optional.ofNullable(group));
		when(problemRepository.findById(10L)).thenReturn(Optional.ofNullable(problem));
		when(solutionRepository.findAllByProblem(problem)).thenReturn(list);
		// when
		List<GetSolutionResponse> result = solutionService.getSolutionList(user,10L);
		// then
		assertThat(result.size()).isEqualTo(30);
		for(int i=0; i<30; i++){
			assertThat(result.get(i).content()).isEqualTo("content"+i);
			assertThat(result.get(i).memoryUsage()).isEqualTo(i);
			assertThat(result.get(i).executionTime()).isEqualTo(i);
			assertThat(result.get(i).nickname()).isEqualTo("nickname");
			assertThat(result.get(i).profileImage()).isEqualTo("profileImage");
			assertThat(result.get(i).language()).isEqualTo("Java"+i);
			assertThat(result.get(i).codeLength()).isEqualTo(i);
		}
	}

	@Test
	@DisplayName("풀이 목록 조회 성공 (멤버)")
	void getSolutionList_2() {
		// given
		List<Solution> list = new ArrayList<>(30);
		for(int i=0; i<30; i++) {
			list.add(Solution.builder()
				.problem(problem)
				.content("content"+i)
				.user(user)
				.memoryUsage(i)
				.executionTime(i)
				.isCorrect(true)
				.language("Java"+i)
				.codeLength(i)
				.build());
		}
		when(studyGroupRepository.findById(30L)).thenReturn(Optional.ofNullable(group));
		when(problemRepository.findById(10L)).thenReturn(Optional.ofNullable(problem));
		when(solutionRepository.findAllByProblem(problem)).thenReturn(list);
		when(groupMemberRepository.existsByUserAndStudyGroup(user2,group)).thenReturn(true);
		// when
		List<GetSolutionResponse> result = solutionService.getSolutionList(user2,10L);
		// then
		assertThat(result.size()).isEqualTo(30);
		for(int i=0; i<30; i++){
			assertThat(result.get(i).content()).isEqualTo("content"+i);
			assertThat(result.get(i).memoryUsage()).isEqualTo(i);
			assertThat(result.get(i).executionTime()).isEqualTo(i);
			assertThat(result.get(i).nickname()).isEqualTo("nickname");
			assertThat(result.get(i).profileImage()).isEqualTo("profileImage");
			assertThat(result.get(i).language()).isEqualTo("Java"+i);
			assertThat(result.get(i).codeLength()).isEqualTo(i);
		}
	}

	@Test
	@DisplayName("풀이 목록 조회 실패 : 존재하지 않는 그룹")
	void getSolutionListFailed_1(){
		// given
		when(problemRepository.findById(10L)).thenReturn(Optional.ofNullable(problem));
		when(studyGroupRepository.findById(30L)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> solutionService.getSolutionList(user,10L))
			.isInstanceOf(StudyGroupValidationException.class)
			.hasFieldOrPropertyWithValue("code",HttpStatus.NOT_FOUND.value())
			.hasFieldOrPropertyWithValue("error","존재하지 않는 그룹 입니다.");
	}

	@Test
	@DisplayName("풀이 목록 조회 실패 : 존재하지 않는 문제")
	void getSolutionListFailed_2(){
		// given
		when(problemRepository.findById(10L)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> solutionService.getSolutionList(user,10L))
			.isInstanceOf(ProblemValidationException.class)
			.hasFieldOrPropertyWithValue("code", HttpStatus.NOT_FOUND.value())
			.hasFieldOrPropertyWithValue("error","존재하지 않는 문제 입니다.");
	}

	@Test
	@DisplayName("풀이 목록 조회 실패 : 참여하지 않은 그룹")
	void getSolutionListFailed_3(){
		// given
		when(problemRepository.findById(10L)).thenReturn(Optional.ofNullable(problem));
		when(studyGroupRepository.findById(30L)).thenReturn(Optional.ofNullable(group));
		when(groupMemberRepository.existsByUserAndStudyGroup(user2,group)).thenReturn(false);
		// when, then
		assertThatThrownBy(() -> solutionService.getSolutionList(user2,10L))
			.isInstanceOf(GroupMemberValidationException.class)
			.hasFieldOrPropertyWithValue("code", HttpStatus.FORBIDDEN.value())
			.hasFieldOrPropertyWithValue("error","참여하지 않은 그룹 입니다.");
	}
}