package com.gamzabat.algohub.exception;

import lombok.Getter;

@Getter
public class StudyGroupValidationException extends RuntimeException{
	private final String error;

	public StudyGroupValidationException(String error) {
		this.error = error;
	}
}
