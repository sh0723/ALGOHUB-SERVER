package com.gamzabat.algohub.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.gamzabat.algohub.domain.Problem;
import com.gamzabat.algohub.domain.StudyGroup;
import com.gamzabat.algohub.domain.User;
import com.gamzabat.algohub.dto.CreateProblemRequest;
import com.gamzabat.algohub.dto.EditProblemRequest;
import com.gamzabat.algohub.enums.Role;
import com.gamzabat.algohub.exception.ProblemValidationException;
import com.gamzabat.algohub.exception.StudyGroupValidationException;
import com.gamzabat.algohub.repository.ProblemRepository;
import com.gamzabat.algohub.repository.StudyGroupRepository;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {
	@InjectMocks
	private ProblemService problemService;
	@Mock
	private ProblemRepository problemRepository;
	@Mock
	private StudyGroupRepository groupRepository;

	private User user;
	private User user2;
	private StudyGroup group;
	private Problem problem;
	@Captor
	private ArgumentCaptor<Problem> problemCaptor;

	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		user = User.builder().email("email1").password("password").nickname("nickname")
			.role(Role.USER).profileImage("image").build();
		user2 = User.builder().email("email2").password("password").nickname("nickname")
			.role(Role.USER).profileImage("image").build();
		group = StudyGroup.builder().name("name").owner(user).groupImage("imageUrl").groupCode("code").build();
		problem = Problem.builder().studyGroup(group).link("link").deadline(LocalDate.now()).build();

		Field userField = User.class.getDeclaredField("id");
		userField.setAccessible(true);
		userField.set(user,1L);
		userField.set(user2,2L);

		Field groupId = StudyGroup.class.getDeclaredField("id");
		groupId.setAccessible(true);
		groupId.set(group,10L);

		Field problemId = Problem.class.getDeclaredField("id");
		problemId.setAccessible(true);
		problemId.set(problem,20L);
	}

	@Test
	@DisplayName("문제 생성 성공")
	void createProblem() {
		// given
		CreateProblemRequest request = CreateProblemRequest.builder()
			.groupId(10L)
			.link("link")
			.deadline(LocalDate.now())
			.build();
		when(groupRepository.findById(10L)).thenReturn(Optional.ofNullable(group));
		// when
		problemService.createProblem(user,request);
		// then
		verify(problemRepository,times(1)).save(problemCaptor.capture());
		Problem result = problemCaptor.getValue();
		assertThat(result.getStudyGroup()).isEqualTo(group);
		assertThat(result.getLink()).isEqualTo("link");
		assertThat(result.getDeadline()).isEqualTo(LocalDate.now());
	}

	@Test
	@DisplayName("문제 생성 실패 : 존재하지 않는 그룹")
	void createProblemFailed_1(){
		// given
		CreateProblemRequest request = CreateProblemRequest.builder()
			.groupId(10L)
			.link("link")
			.deadline(LocalDate.now())
			.build();
		when(groupRepository.findById(10L)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> problemService.createProblem(user,request))
			.isInstanceOf(StudyGroupValidationException.class)
			.hasFieldOrPropertyWithValue("code", HttpStatus.NOT_FOUND.value())
			.hasFieldOrPropertyWithValue("error","존재하지 않는 그룹 입니다.");
	}
	@Test
	@DisplayName("문제 생성 실패 : 권한 없음")
	void createProblemFailed_2(){
		// given
		CreateProblemRequest request = CreateProblemRequest.builder()
			.groupId(10L)
			.link("link")
			.deadline(LocalDate.now())
			.build();
		when(groupRepository.findById(10L)).thenReturn(Optional.ofNullable(group));
		// when, then
		assertThatThrownBy(() -> problemService.createProblem(user2,request))
			.isInstanceOf(StudyGroupValidationException.class)
			.hasFieldOrPropertyWithValue("code", HttpStatus.FORBIDDEN.value())
			.hasFieldOrPropertyWithValue("error","문제에 대한 권한이 없습니다. : create");
	}

	@Test
	@DisplayName("문제 마감 기한 수정 성공")
	void editProblem(){
		// given
		EditProblemRequest request = EditProblemRequest.builder()
			.problemId(20L)
			.deadline(LocalDate.now().plusDays(3))
			.build();
		when(problemRepository.findById(20L)).thenReturn(Optional.ofNullable(problem));
		when(groupRepository.findById(10L)).thenReturn(Optional.ofNullable(group));
		// when
		problemService.editProblem(user,request);
		// then
		assertThat(problem.getDeadline()).isEqualTo(request.deadline());
	}

	@Test
	@DisplayName("문제 마감 기한 수정 실패 : 존재하지 않는 그룹")
	void editProblemFailed_1(){
		// given
		EditProblemRequest request = EditProblemRequest.builder()
			.problemId(20L)
			.deadline(LocalDate.now().plusDays(3))
			.build();
		when(problemRepository.findById(20L)).thenReturn(Optional.ofNullable(problem));
		when(groupRepository.findById(10L)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> problemService.editProblem(user, request))
			.isInstanceOf(StudyGroupValidationException.class)
			.hasFieldOrPropertyWithValue("code",HttpStatus.NOT_FOUND.value())
			.hasFieldOrPropertyWithValue("error","존재하지 않는 그룹 입니다.");
	}

	@Test
	@DisplayName("문제 마감 기한 수정 실패 : 존재하지 않는 문제")
	void editProblemFailed_2(){
		// given
		EditProblemRequest request = EditProblemRequest.builder()
			.problemId(20L)
			.deadline(LocalDate.now().plusDays(3))
			.build();
		when(problemRepository.findById(20L)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> problemService.editProblem(user, request))
			.isInstanceOf(ProblemValidationException.class)
			.hasFieldOrPropertyWithValue("code",HttpStatus.NOT_FOUND.value())
			.hasFieldOrPropertyWithValue("error","존재하지 않는 문제 입니다.");
	}

	@Test
	@DisplayName("문제 마감 기한 수정 실패 : 권한 없음")
	void editProblemFailed_3(){
		// given
		EditProblemRequest request = EditProblemRequest.builder()
			.problemId(20L)
			.deadline(LocalDate.now().plusDays(3))
			.build();
		when(problemRepository.findById(20L)).thenReturn(Optional.ofNullable(problem));
		when(groupRepository.findById(10L)).thenReturn(Optional.ofNullable(group));
		// when, then
		assertThatThrownBy(() -> problemService.editProblem(user2, request))
			.isInstanceOf(StudyGroupValidationException.class)
			.hasFieldOrPropertyWithValue("code",HttpStatus.FORBIDDEN.value())
			.hasFieldOrPropertyWithValue("error","문제에 대한 권한이 없습니다. : edit");
	}



}