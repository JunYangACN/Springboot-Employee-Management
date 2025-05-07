package com.azid.springboot.assessment.employee_management.exception;

public class DatabaseException extends RuntimeException {
  public DatabaseException(String message, Throwable cause) {
    super(message, cause);
  }
}