package com.gamzabat.algohub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamzabat.algohub.common.annotation.AuthedUser;
import com.gamzabat.algohub.domain.User;
import com.gamzabat.algohub.dto.CreateCommentRequest;
import com.gamzabat.algohub.exception.RequestException;
import com.gamzabat.algohub.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Tag(name = "댓글 API", description = "풀이에 대한 댓글 관련 API")
public class CommentController {
	private final CommentService commentService;

	@PostMapping
	@Operation(summary = "댓글 작성 API")
	public ResponseEntity<Object> createComment(@AuthedUser User user,
		@Valid @RequestBody CreateCommentRequest request, Errors errors){
		if(errors.hasErrors())
			throw new RequestException("댓글 작성 요청이 올바르지 않습니다.",errors);
		commentService.createComment(user,request);
		return ResponseEntity.ok().body("OK");
	}
}
