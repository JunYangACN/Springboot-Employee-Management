package com.azid.springboot.assessment.employee_management.exception;

public class PhoneNoAlreadyExistsException extends RuntimeException {
    public PhoneNoAlreadyExistsException(String message) {
        super(message);
    }
}
