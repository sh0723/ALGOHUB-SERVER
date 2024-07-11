package com.gamzabat.algohub.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
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

import com.gamzabat.algohub.domain.Comment;
import com.gamzabat.algohub.domain.Problem;
import com.gamzabat.algohub.domain.Solution;
import com.gamzabat.algohub.domain.StudyGroup;
import com.gamzabat.algohub.domain.User;
import com.gamzabat.algohub.dto.CreateCommentRequest;
import com.gamzabat.algohub.enums.Role;
import com.gamzabat.algohub.exception.GroupMemberValidationException;
import com.gamzabat.algohub.exception.ProblemValidationException;
import com.gamzabat.algohub.exception.SolutionValidationException;
import com.gamzabat.algohub.exception.StudyGroupValidationException;
import com.gamzabat.algohub.repository.CommentRepository;
import com.gamzabat.algohub.repository.GroupMemberRepository;
import com.gamzabat.algohub.repository.ProblemRepository;
import com.gamzabat.algohub.repository.SolutionRepository;
import com.gamzabat.algohub.repository.StudyGroupRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
	@InjectMocks
	private CommentService commentService;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private StudyGroupRepository studyGroupRepository;
	@Mock
	private GroupMemberRepository groupMemberRepository;
	@Mock
	private SolutionRepository solutionRepository;
	@Mock
	private ProblemRepository problemRepository;
	private User user, user2;
	private Solution solution;
	private Problem problem;
	private StudyGroup studyGroup;
	@Captor
	private ArgumentCaptor<Comment> commentCaptor;
	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		user = User.builder().email("email1").password("password").nickname("nickname")
			.role(Role.USER).profileImage("image").build();
		user2 = User.builder().email("email2").password("password").nickname("nickname")
			.role(Role.USER).profileImage("image").build();
		studyGroup = StudyGroup.builder().owner(user).build();
		problem = Problem.builder().studyGroup(studyGroup).build();
		solution = Solution.builder().problem(problem).content("solution").build();

		Field userField = User.class.getDeclaredField("id");
		userField.setAccessible(true);
		userField.set(user,1L);

		Field solutionField = Solution.class.getDeclaredField("id");
		solutionField.setAccessible(true);
		solutionField.set(solution,10L);

		Field problemField = Problem.class.getDeclaredField("id");
		problemField.setAccessible(true);
		problemField.set(problem,20L);

		Field groupField = StudyGroup.class.getDeclaredField("id");
		groupField.setAccessible(true);
		groupField.set(studyGroup,30L);
	}

	@Test
	@DisplayName("댓글 작성 성공 (주인)")
	void createComment() {
		// given
		CreateCommentRequest request = CreateCommentRequest.builder().solutionId(10L).content("content").build();
		when(solutionRepository.findById(10L)).thenReturn(Optional.ofNullable(solution));
		when(problemRepository.findById(20L)).thenReturn(Optional.ofNullable(problem));
		when(studyGroupRepository.findById(30L)).thenReturn(Optional.ofNullable(studyGroup));
		// when
		commentService.createComment(user,request);
		// then
		verify(commentRepository,times(1)).save(commentCaptor.capture());
		Comment result = commentCaptor.getValue();
		assertThat(result.getContent()).isEqualTo("content");
		assertThat(result.getUser()).isEqualTo(user);
		assertThat(result.getSolution()).isEqualTo(solution);
	}

	@Test
	@DisplayName("댓글 작성 성공 (멤버)")
	void createComment_2() {
		// given
		CreateCommentRequest request = CreateCommentRequest.builder().solutionId(10L).content("content").build();
		when(solutionRepository.findById(10L)).thenReturn(Optional.ofNullable(solution));
		when(problemRepository.findById(20L)).thenReturn(Optional.ofNullable(problem));
		when(studyGroupRepository.findById(30L)).thenReturn(Optional.ofNullable(studyGroup));
		when(groupMemberRepository.existsByUserAndStudyGroup(user2,studyGroup)).thenReturn(true);
		// when
		commentService.createComment(user2,request);
		// then
		verify(commentRepository,times(1)).save(commentCaptor.capture());
		Comment result = commentCaptor.getValue();
		assertThat(result.getContent()).isEqualTo("content");
		assertThat(result.getUser()).isEqualTo(user2);
		assertThat(result.getSolution()).isEqualTo(solution);
	}

	@Test
	@DisplayName("댓글 작성 실패 : 존재하지 않는 풀이")
	void createCommentFailed_1(){
		// given
		CreateCommentRequest request = CreateCommentRequest.builder().solutionId(10L).content("content").build();
		when(solutionRepository.findById(10L)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> commentService.createComment(user,request))
			.isInstanceOf(SolutionValidationException.class)
			.hasFieldOrPropertyWithValue("error","존재하지 않는 풀이 입니다.");
	}

	@Test
	@DisplayName("댓글 작성 실패 : 존재하지 않는 문제")
	void createCommentFailed_2(){
		// given
		CreateCommentRequest request = CreateCommentRequest.builder().solutionId(10L).content("content").build();
		when(solutionRepository.findById(10L)).thenReturn(Optional.ofNullable(solution));
		when(problemRepository.findById(20L)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> commentService.createComment(user,request))
			.isInstanceOf(ProblemValidationException.class)
			.hasFieldOrPropertyWithValue("code", HttpStatus.NOT_FOUND.value())
			.hasFieldOrPropertyWithValue("error","존재하지 않는 문제 입니다.");
	}

	@Test
	@DisplayName("댓글 작성 실패 : 존재하지 않는 그룹")
	void createCommentFailed_3(){
		// given
		CreateCommentRequest request = CreateCommentRequest.builder().solutionId(10L).content("content").build();
		when(solutionRepository.findById(10L)).thenReturn(Optional.ofNullable(solution));
		when(problemRepository.findById(20L)).thenReturn(Optional.ofNullable(problem));
		when(studyGroupRepository.findById(30L)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> commentService.createComment(user,request))
			.isInstanceOf(StudyGroupValidationException.class)
			.hasFieldOrPropertyWithValue("code", HttpStatus.NOT_FOUND.value())
			.hasFieldOrPropertyWithValue("error","존재하지 않는 그룹 입니다.");
	}

	@Test
	@DisplayName("댓글 작성 실패 : 참여하지 않은 그룹")
	void createCommentFailed_4(){
		// given
		CreateCommentRequest request = CreateCommentRequest.builder().solutionId(10L).content("content").build();
		when(solutionRepository.findById(10L)).thenReturn(Optional.ofNullable(solution));
		when(problemRepository.findById(20L)).thenReturn(Optional.ofNullable(problem));
		when(studyGroupRepository.findById(30L)).thenReturn(Optional.ofNullable(studyGroup));
		when(groupMemberRepository.existsByUserAndStudyGroup(user2,studyGroup)).thenReturn(false);
		// when, then
		assertThatThrownBy(() -> commentService.createComment(user2,request))
			.isInstanceOf(GroupMemberValidationException.class)
			.hasFieldOrPropertyWithValue("code", HttpStatus.FORBIDDEN.value())
			.hasFieldOrPropertyWithValue("error","참여하지 않은 그룹 입니다.");
	}

}