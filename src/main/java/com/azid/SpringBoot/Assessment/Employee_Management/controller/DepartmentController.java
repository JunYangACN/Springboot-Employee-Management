package com.azid.springboot.assessment.employee_management.controller;

import com.azid.springboot.assessment.employee_management.dto.DepartmentDTO;
import com.azid.springboot.assessment.employee_management.dto.DepartmentResponseDTO;
import com.azid.springboot.assessment.employee_management.dto.UpdateDepartmentDTO;
import com.azid.springboot.assessment.employee_management.exception.DepartmentNotFoundException;
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
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(departmentDTO);

        log.info("Received request to add department: {}", body);
        DepartmentDTO addedDepartment = departmentService.addDepartment(departmentDTO);
        log.info("Added Department: {}", addedDepartment);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedDepartment);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartment(@PathVariable Long id) {
        try {
            DepartmentResponseDTO department = departmentService.getDepartmentById(id);
            return ResponseEntity.ok(department);
        } catch (DepartmentNotFoundException e) {
            log.error("Department not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/getAll")
    public ResponseEntity<?> getAllDepartment() {
        List<DepartmentResponseDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @PutMapping("/update-department/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody @Valid UpdateDepartmentDTO updatedepartmentDTO) {
        try {
            UpdateDepartmentDTO updated = departmentService.updateDepartment(id, updatedepartmentDTO);
            return ResponseEntity.ok(updated);
        } catch (DepartmentNotFoundException e) {
            log.error("Update failed - Department not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @DeleteMapping("/delete-department/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok("Department with ID " + id + " deleted successfully.");
        } catch (DepartmentNotFoundException e) {
            log.error("Delete failed - Department not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}