package com.gamzabat.algohub.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.gamzabat.algohub.domain.Problem;
import com.gamzabat.algohub.domain.Solution;
import com.gamzabat.algohub.domain.User;
import com.gamzabat.algohub.dto.GetSolutionResponse;
import com.gamzabat.algohub.enums.Role;
import com.gamzabat.algohub.exception.ProblemValidationException;
import com.gamzabat.algohub.exception.UserValidationException;
import com.gamzabat.algohub.repository.ProblemRepository;
import com.gamzabat.algohub.repository.SolutionRepository;
import com.gamzabat.algohub.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SolutionServiceTest {
	@InjectMocks
	private SolutionService solutionService;
	@Mock
	private SolutionRepository solutionRepository;
	@Mock
	private ProblemRepository problemRepository;
	@Mock
	private UserRepository userRepository;
	private User user;
	private Problem problem;
	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		user = User.builder().email("email1").password("password").nickname("nickname")
			.role(Role.USER).profileImage("image").build();
		problem = Problem.builder().link("link").deadline(LocalDate.now()).build();

		Field userField = User.class.getDeclaredField("id");
		userField.setAccessible(true);
		userField.set(user,1L);
		Field problemField = Problem.class.getDeclaredField("id");
		problemField.setAccessible(true);
		problemField.set(problem,10L);
	}

	@Test
	@DisplayName("풀이 목록 조회 성공")
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
				.build());
		}
		when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
		when(problemRepository.findById(10L)).thenReturn(Optional.ofNullable(problem));
		when(solutionRepository.findAllByUserAndProblem(user,problem)).thenReturn(list);
		// when
		List<GetSolutionResponse> result = solutionService.getSolutionList(1L,10L);
		// then
		assertThat(result.size()).isEqualTo(30);
		for(int i=0; i<30; i++){
			assertThat(result.get(i).content()).isEqualTo("content"+i);
			assertThat(result.get(i).memoryUsage()).isEqualTo(i);
			assertThat(result.get(i).executionTime()).isEqualTo(i);
		}
	}

	@Test
	@DisplayName("풀이 목록 조회 실패 : 존재하지 않는 회원")
	void getSolutionListFailed_1(){
		// given
		when(userRepository.findById(1L)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> solutionService.getSolutionList(1L,10L))
			.isInstanceOf(UserValidationException.class)
			.hasFieldOrPropertyWithValue("errors","존재하지 않는 회원 입니다.");
	}

	@Test
	@DisplayName("풀이 목록 조회 실패 : 존재하지 않는 문제")
	void getSolutionListFailed_2(){
		// given
		when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
		when(problemRepository.findById(10L)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> solutionService.getSolutionList(1L,10L))
			.isInstanceOf(ProblemValidationException.class)
			.hasFieldOrPropertyWithValue("code", HttpStatus.NOT_FOUND.value())
			.hasFieldOrPropertyWithValue("error","존재하지 않는 문제 입니다.");
	}
}