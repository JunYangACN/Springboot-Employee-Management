package com.azid.springboot.assessment.employee_management.controller;

import com.azid.springboot.assessment.employee_management.dto.DepartmentDTO;
import com.azid.springboot.assessment.employee_management.service.DepartmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {
    private static final Logger log = LoggerFactory.getLogger(DepartmentController.class);
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping("/create-department")
    public ResponseEntity<DepartmentDTO> addDepartment(@RequestBody @Valid DepartmentDTO departmentDTO) throws JsonProcessingException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(departmentDTO);

            log.info("Received request to add department: {}", body);
            DepartmentDTO addedDepartment = departmentService.addDepartment(departmentDTO);
            log.info("Added Department: {}", addedDepartment);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedDepartment);
        } catch (Exception e) {
            log.error("Error adding department: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartment(@PathVariable Long id) {
        try {
            DepartmentDTO department = departmentService.getDepartmentById(id);
            return ResponseEntity.ok(department);
        } catch (IllegalArgumentException e) {
            log.error("Department not found for ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department not found");
        } catch (Exception e) {
            log.error("Error retrieving department: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving department");
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllDepartment() {
        try {
            List<DepartmentDTO> departments = departmentService.getAllDepartments();
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            log.error("Error retrieving all departments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving departments");
        }
    }

    @PutMapping("/update-department/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody @Valid DepartmentDTO departmentDTO) {
        try {
            DepartmentDTO updated = departmentService.updateDepartment(id, departmentDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Department not found for ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department not found");
        } catch (Exception e) {
            log.error("Error updating department: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating department");
        }
    }

    @DeleteMapping("/delete-department/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok("Department ID: " + id + " deleted");
        } catch (IllegalArgumentException e) {
            log.error("Department not found for ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department not found");
        } catch (Exception e) {
            log.error("Error deleting department: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting department");
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        log.error("Validation error: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleGeneralExceptions(Exception ex) {
        log.error("An error occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }
}