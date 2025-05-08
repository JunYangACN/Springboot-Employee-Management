package com.azid.springboot.assessment.employee_management.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    // Helper method to build standardized response
    private Map<String, Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return response;
    }

    // Handles validation errors (e.g. @NotBlank)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        err -> err.getField(),
                        err -> err.getDefaultMessage(),
                        (existing, replacement) -> existing
                ));

        response.put("errorDetails", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles database constraint violations (foreign key, unique, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException e) {
        String rootMessage = (e.getRootCause() != null) ? e.getRootCause().getMessage() : e.getMessage();
        return new ResponseEntity<>(buildResponse(HttpStatus.CONFLICT, "Data integrity violation: " + rootMessage), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmployeeAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmployeeAlreadyExists(EmployeeAlreadyExistsException e) {
        return new ResponseEntity<>(buildResponse(HttpStatus.CONFLICT, e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Object> handleEmployeeNotFound(EmployeeNotFoundException e) {
        return new ResponseEntity<>(buildResponse(HttpStatus.NOT_FOUND, e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DepartmentAlreadyExistsException.class)
    public ResponseEntity<Object> handleDepartmentAlreadyExists(DepartmentAlreadyExistsException e) {
        return new ResponseEntity<>(buildResponse(HttpStatus.CONFLICT, e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<Object> handleDepartmentNotFound(DepartmentNotFoundException e) {
        return new ResponseEntity<>(buildResponse(HttpStatus.NOT_FOUND, e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DepartmentInUseException.class)
    public ResponseEntity<Object> handleDepartmentInUse(DepartmentInUseException e) {
        return new ResponseEntity<>(buildResponse(HttpStatus.CONFLICT, e.getMessage()), HttpStatus.CONFLICT);
    }

    // Catch-all fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception e) {
        return new ResponseEntity<>(buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<Object> handleDatabase(DatabaseException e) {
        return new ResponseEntity<>(buildResponse(HttpStatus.CONFLICT, e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleService(ServiceException e) {
        return new ResponseEntity<>(buildResponse(HttpStatus.CONFLICT, e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnexpectedServiceException.class)
    public ResponseEntity<Object> handleUnexpectedService(UnexpectedServiceException e) {
        return new ResponseEntity<>(buildResponse(HttpStatus.CONFLICT, e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException e) {
        return new ResponseEntity<>(buildResponse(HttpStatus.CONFLICT, e.getMessage()), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return new ResponseEntity<>(buildResponse(HttpStatus.CONFLICT, e.getMessage()), HttpStatus.CONFLICT);
    }

}
