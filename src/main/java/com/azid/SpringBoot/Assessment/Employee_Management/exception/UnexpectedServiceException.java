package com.azid.springboot.assessment.employee_management.exception;

public class UnexpectedServiceException extends RuntimeException {
    public UnexpectedServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}