package com.gamzabat.algohub.feature.comment.dto;

import java.time.LocalDateTime;

import com.gamzabat.algohub.feature.comment.domain.Comment;

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
