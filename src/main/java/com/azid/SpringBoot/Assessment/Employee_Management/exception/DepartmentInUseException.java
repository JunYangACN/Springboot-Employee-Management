package com.azid.springboot.assessment.employee_management.exception;

public class DepartmentInUseException extends RuntimeException {
    public DepartmentInUseException(String message) {
        super(message);
    }
}
