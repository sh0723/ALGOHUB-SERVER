package com.gamzabat.algohub.controller;

import com.gamzabat.algohub.common.jwt.TokenProvider;
import com.gamzabat.algohub.dto.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.gamzabat.algohub.common.annotation.AuthedUser;
import com.gamzabat.algohub.domain.User;
import com.gamzabat.algohub.exception.RequestException;
import com.gamzabat.algohub.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "회원 컨트롤러", description = "회원 관련된 API 명세서")
public class UserController {
	private final UserService userService;
	private final TokenProvider tokenProvider;

	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "회원 가입 API")
	public ResponseEntity<Object> register(@Valid @RequestPart RegisterRequest request, Errors errors,
		@RequestPart(required = false) MultipartFile profileImage){
		if(errors.hasErrors())
			throw new RequestException("올바르지 않은 요청입니다.",errors);
		userService.register(request, profileImage);
		return ResponseEntity.ok().body("OK");
	}

	@PostMapping(value = "/sign-in")
	@Operation(summary = "로그인 API")
	public ResponseEntity<Object> signIn(@Valid @RequestBody SignInRequest request, Errors errors){
		if(errors.hasErrors())
			throw new RequestException("로그인 요청이 올바르지 않습니다.",errors);
		SignInResponse response = userService.signIn(request);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping(value = "/user-info")
	public ResponseEntity<UserInfoResponse> userInfo(@RequestHeader("Authorization") String token){
		String email = tokenProvider.getUserEmail(token);
		UserInfoResponse userInfo = userService.userInfo(email);
		return ResponseEntity.ok().body(userInfo);
	}

	@PatchMapping(value = "/update-user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateInfo(@RequestHeader("Authorization") String token, @Valid @RequestPart UpdateRequest updateRequest, Errors errors, @RequestPart(required = false) MultipartFile profileImage){

		if (errors.hasErrors()) {
			throw new RequestException("올바르지 않은 요청입니다.", errors);
		}

		String email = tokenProvider.getUserEmail(token);
		userService.userUpdate(email, updateRequest,profileImage);

		return ResponseEntity.ok().body("OK");
	}

	@DeleteMapping(value = "/delete-user")
	public ResponseEntity<Object> deleteUser(@RequestHeader("Authorization") String token, @Valid @RequestPart DeleteRequest request, Errors errors){
		if (errors.hasErrors()) {
			throw new RequestException("올바르지 않은 요청입니다.",errors);
		}

		String email = tokenProvider.getUserEmail(token);
		if (userService.deleteUser(email, request))
		{
			return ResponseEntity.ok().body("OK");
		}
		else
		{
			return ResponseEntity.ok().body("다시 입력하세요");
		}
	}

	@GetMapping(value = "/test")
	@Operation(summary = "테스트 API")
	public ResponseEntity<Object> test(@AuthedUser User user){
		return ResponseEntity.ok().body(user.getEmail());
	}
}
