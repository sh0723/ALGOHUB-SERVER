package com.gamzabat.algohub.feature.user.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamzabat.algohub.common.annotation.AuthedUserResolver;
import com.gamzabat.algohub.common.jwt.TokenProvider;
import com.gamzabat.algohub.config.SpringSecurityConfig;
import com.gamzabat.algohub.exception.UserValidationException;
import com.gamzabat.algohub.feature.image.service.ImageService;
import com.gamzabat.algohub.feature.user.domain.User;
import com.gamzabat.algohub.feature.user.dto.DeleteUserRequest;
import com.gamzabat.algohub.feature.user.dto.RegisterRequest;
import com.gamzabat.algohub.feature.user.dto.SignInRequest;
import com.gamzabat.algohub.feature.user.dto.SignInResponse;
import com.gamzabat.algohub.feature.user.dto.UpdateUserRequest;
import com.gamzabat.algohub.feature.user.dto.UserInfoResponse;
import com.gamzabat.algohub.feature.user.exception.UncorrectedPasswordException;
import com.gamzabat.algohub.feature.user.repository.UserRepository;
import com.gamzabat.algohub.feature.user.service.UserService;

@WebMvcTest(UserController.class)
@WithMockUser
@Import(SpringSecurityConfig.class)
class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private UserService userService;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private PasswordEncoder passwordEncoder;
	@MockBean
	private ImageService imageService;
	@MockBean
	private TokenProvider tokenProvider;
	@MockBean
	private AuthenticationManagerBuilder authManager;
	@Autowired
	private AuthedUserResolver authedUserResolver;

	private User user;
	private String token;

	@BeforeEach
	void setUp() {
		user = User.builder().email("email").password("password").build();

		token = "token";
		when(tokenProvider.getUserEmail(token)).thenReturn("email");
		when(userRepository.findByEmail("email")).thenReturn(Optional.ofNullable(user));
	}

	@Test
	@DisplayName("회원 가입 성공")
	void register() throws Exception {
		// given
		RegisterRequest request = new RegisterRequest("email","password","nickname","bojNickname");
		String requestJson = objectMapper.writeValueAsString(request);
		MockMultipartFile requestPart = new MockMultipartFile("request","","application/json",requestJson.getBytes());
		MockMultipartFile profileImage = new MockMultipartFile("profileImage","profile.jpg","image/jpeg","image".getBytes());

		doNothing().when(userService).register(any(RegisterRequest.class),any(MultipartFile.class));
		// when, then
		mockMvc.perform(multipart("/api/user/register")
				.file(requestPart)
				.file(profileImage)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isOk())
			.andExpect(content().string("OK"));

		verify(userService,times(1)).register(any(RegisterRequest.class),any(MultipartFile.class));
	}

	@Test
	@DisplayName("회원 가입 성공 : 프로필 사진 X")
	void register_2() throws Exception {
		// given
		RegisterRequest request = new RegisterRequest("email","password","nickname","bojNickname");
		String requestJson = objectMapper.writeValueAsString(request);
		MockMultipartFile requestPart = new MockMultipartFile("request","","application/json",requestJson.getBytes());

		doNothing().when(userService).register(any(RegisterRequest.class),any());
		// when, then
		mockMvc.perform(multipart("/api/user/register")
				.file(requestPart)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isOk())
			.andExpect(content().string("OK"));

		verify(userService,times(1)).register(any(RegisterRequest.class),any());
	}

	@ParameterizedTest
	@CsvSource(value = {
		" ' ', password, nickname, bjNickname, email : 이메일은 필수 입력입니다.",
		"email, ' ', nickname, bjNickname, password : 비밀번호는 필수 입력입니다.",
		"email, password, ' ', bjNickname, nickname : 닉네임은 필수 입력입니다.",
		"email, password, nickname, ' ', bjNickname : 백준 닉네임은 필수 입력입니다."
	}, nullValues = "null")
	@DisplayName("회원 가입 실패 : 잘못된 요청")
	void registerFailed_1(String email, String password, String nickname, String bjNickname, String exceptionMessage) throws Exception {
		// given
		RegisterRequest request = new RegisterRequest(email,password,nickname,bjNickname);
		String requestJson = objectMapper.writeValueAsString(request);
		MockMultipartFile requestPart = new MockMultipartFile("request","","application/json",requestJson.getBytes());
		MockMultipartFile profileImage = new MockMultipartFile("profileImage","profile.jpg","image/jpeg","image".getBytes());
		// when, then
		mockMvc.perform(multipart("/api/user/register")
			.file(requestPart)
			.file(profileImage)
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.error").value("올바르지 않은 요청입니다."))
			.andExpect(jsonPath("$.messages", hasItem(exceptionMessage)));
	}

	@Test
	@DisplayName("회원가입 실패 : 이미 가입 된 이메일")
	void registerFailed_2() throws Exception {
		// given
		RegisterRequest request = new RegisterRequest("duplicatedEmail","password","nickname","bjNickname");
		String requestJson = objectMapper.writeValueAsString(request);
		MockMultipartFile requestPart = new MockMultipartFile("request","","application/json",requestJson.getBytes());
		MockMultipartFile profileImage = new MockMultipartFile("profileImage","profile.jpg","image/jpeg","image".getBytes());
		doThrow(new UserValidationException("이미 가입 된 이메일 입니다.")).when(userService).register(any(RegisterRequest.class),any(MultipartFile.class));
		// when, then
		mockMvc.perform(multipart("/api/user/register")
				.file(requestPart)
				.file(profileImage)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.error").value("이미 가입 된 이메일 입니다."));

		verify(userService,times(1)).register(request,profileImage);
	}

	@Test
	@DisplayName("로그인 성공")
	void signIn() throws Exception {
		// given
		SignInRequest request = new SignInRequest("email","password");
		SignInResponse response = new SignInResponse("token");
		when(userService.signIn(any(SignInRequest.class))).thenReturn(response);
		// when, then
		mockMvc.perform(post("/api/user/sign-in")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value("token"));

		verify(userService,times(1)).signIn(request);
	}

	@ParameterizedTest
	@CsvSource(value = {
		"'',password, email : 이메일은 필수 입력 입니다.",
		"email,'', password : 비밀번호는 필수 입력 입니다."
	},nullValues = "null")
	@DisplayName("로그인 실패 : 잘못된 요청")
	void signInFailed_1(String email, String password, String exceptionMessage) throws Exception {
		// given
		SignInRequest request = new SignInRequest(email,password);
		// when, then
		mockMvc.perform(post("/api/user/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.error").value("로그인 요청이 올바르지 않습니다."))
			.andExpect(jsonPath("$.messages",hasItem(exceptionMessage)));
	}

	@Test
	@DisplayName("로그인 실패 : 존재하지 않는 회원")
	void signInFailed_2() throws Exception {
		// given
		SignInRequest request = new SignInRequest("invalidEmail","password");
		doThrow(new UserValidationException("존재하지 않는 회원 입니다.")).when(userService).signIn(any(SignInRequest.class));
		// when, then
		mockMvc.perform(post("/api/user/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.error").value("존재하지 않는 회원 입니다."));
		verify(userService,times(1)).signIn(any(SignInRequest.class));
	}

	@Test
	@DisplayName("로그인 실패 : 비밀번호 틀림")
	void signInFailed_3() throws Exception {
		// given
		SignInRequest request = new SignInRequest("email","invalidPassword");
		doThrow(new UncorrectedPasswordException("비밀번호가 틀렸습니다.")).when(userService).signIn(any(SignInRequest.class));
		// when, then
		mockMvc.perform(post("/api/user/sign-in")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.error").value("비밀번호가 틀렸습니다."));
		verify(userService,times(1)).signIn(any(SignInRequest.class));
	}

	@Test
	@DisplayName("회원 정보 수정 성공")
	void updateUser() throws Exception {
		// given
		UpdateUserRequest request = new UpdateUserRequest();
		request.setNickname("nickname");
		request.setBjNickname("bjNickname");
		MockMultipartFile requestPart = new MockMultipartFile("request","","application/json",objectMapper.writeValueAsString(request).getBytes());
		MockMultipartFile profileImage = new MockMultipartFile("profileImage","profile.jpg","image/jpeg","image".getBytes());
		// when, then
		mockMvc.perform(multipart("/api/user")
				.file(requestPart)
				.file(profileImage)
				.header("Authorization",token)
				.with(request1 -> {
					request1.setMethod("PATCH");
					return request1;
				})
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isOk())
			.andExpect(content().string("OK"));

		verify(userService,times(1)).userUpdate(any(User.class),any(UpdateUserRequest.class),any(MultipartFile.class));
	}

	@Test
	@DisplayName("회원 정보 조회 성공")
	void getUserInfo() throws Exception {
		// given
		UserInfoResponse response = new UserInfoResponse("email","nickname","profileImage","bjNickname");
		when(userService.userInfo(user)).thenReturn(response);
		// when, then
		mockMvc.perform(get("/api/user")
			.header("Authorization",token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("email"))
			.andExpect(jsonPath("$.nickname").value("nickname"))
			.andExpect(jsonPath("$.profileImage").value("profileImage"))
			.andExpect(jsonPath("$.bjNickname").value("bjNickname"));

		verify(userService,times(1)).userInfo(any(User.class));
	}

	@Test
	@DisplayName("회원 탈퇴 성공")
	void deleteUser() throws Exception {
		// given
		DeleteUserRequest request = new DeleteUserRequest("password");
		// when, then
		mockMvc.perform(delete("/api/user")
			.header("Authorization",token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().string("회원정보를 삭제했습니다."));

		verify(userService,times(1)).deleteUser(user,request);
	}

	@Test
	@DisplayName("회원 탈퇴 실패 : 잘못된 요청")
	void deleteUserFailed_1() throws Exception {
		// given
		DeleteUserRequest request = new DeleteUserRequest("");
		// when, then
		mockMvc.perform(delete("/api/user")
				.header("Authorization",token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("올바르지 않은 요청입니다."))
			.andExpect(jsonPath("$.messages",hasItem("password : 비밀번호는 필수 입력입니다.")));
	}

	@Test
	@DisplayName("회원 탈퇴 실패 : 틀린 비밀번호")
	void deleteUserFailed_2() throws Exception {
		// given
		DeleteUserRequest request = new DeleteUserRequest("invalidPassword");
		doThrow(new UncorrectedPasswordException("비밀번호가 틀렸습니다.")).when(userService).deleteUser(user,request);
		// when, then
		mockMvc.perform(delete("/api/user")
				.header("Authorization",token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("비밀번호가 틀렸습니다."));

		verify(userService,times(1)).deleteUser(any(User.class),any(DeleteUserRequest.class));
	}
}