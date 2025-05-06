package com.azid.SpringBoot.Assessment.Employee_Management.controller;

import com.azid.SpringBoot.Assessment.Employee_Management.dto.DepartmentDTO;
import com.azid.SpringBoot.Assessment.Employee_Management.service.DepartmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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

        log.info( "Received request to add department: {}", body);
        DepartmentDTO addedDepartment = departmentService.addDepartment(departmentDTO);
        log.info("Added Department: {}", addedDepartment);
        return ResponseEntity.status(201).body(addedDepartment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable Long id) {
        DepartmentDTO department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartment() {
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @PutMapping("/update-department/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id, @RequestBody @Valid DepartmentDTO departmentDTO) {
        DepartmentDTO updated = departmentService.updateDepartment(id, departmentDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete-department/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity
                .ok("Department ID: " + id + "deleted");
    }
}