package com.gamzabat.algohub.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gamzabat.algohub.dto.RegisterRequest;
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

	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "회원 가입 API")
	public ResponseEntity<Object> register(@Valid @RequestPart RegisterRequest request, Errors errors,
		@RequestPart(required = false) MultipartFile profileImage){
		if(errors.hasErrors())
			throw new RequestException("올바르지 않은 요청입니다.",errors);
		userService.register(request, profileImage);
		return ResponseEntity.ok().body("OK");
	}
}
