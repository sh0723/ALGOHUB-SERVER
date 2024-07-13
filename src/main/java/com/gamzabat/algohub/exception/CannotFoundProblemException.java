package com.gamzabat.algohub.exception;

import lombok.Getter;

@Getter
public class CannotFoundProblemException extends RuntimeException{
    private final String errors;

    public CannotFoundProblemException(String errors) {
        this.errors = errors;
    }
}
