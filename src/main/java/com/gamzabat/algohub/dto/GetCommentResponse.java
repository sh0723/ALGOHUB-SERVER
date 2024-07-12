package com.gamzabat.algohub.dto;

import java.time.LocalDateTime;

import com.gamzabat.algohub.domain.Comment;

import lombok.Builder;

@Builder
public record GetCommentResponse(Long commentId,
								 String writerNickname,
								 String content,
								 LocalDateTime createdAt) {
	public static GetCommentResponse toDTO(Comment comment){
		return GetCommentResponse.builder()
			.commentId(comment.getId())
			.writerNickname(comment.getUser().getNickname())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.build();
	}
}
