package com.azid.SpringBoot.Assessment.Employee_Management.service;

import com.azid.SpringBoot.Assessment.Employee_Management.dto.DepartmentDTO;
import com.azid.SpringBoot.Assessment.Employee_Management.entity.Department;
import com.azid.SpringBoot.Assessment.Employee_Management.exception.DepartmentAlreadyExistsException;
import com.azid.SpringBoot.Assessment.Employee_Management.exception.DepartmentNotFoundException;
import com.azid.SpringBoot.Assessment.Employee_Management.mapper.DepartmentMapper;
import com.azid.SpringBoot.Assessment.Employee_Management.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {
    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    public DepartmentDTO addDepartment(DepartmentDTO departmentDTO) {
        log.info("Adding department: {}", departmentDTO.getName());
        boolean exists = departmentRepository.existsByName(departmentDTO.getName());
        if (exists) {
            log.warn("Department with name '{}' already exists", departmentDTO.getName());
            throw new DepartmentAlreadyExistsException("Department with name '" + departmentDTO.getName() + "' already exists");
        }

        Department department = departmentMapper.toEntity(departmentDTO);
        Department saved = departmentRepository.save(department);
        log.info("Department '{}' added successfully", saved.getName());
        return departmentMapper.toDto(saved);
    }

    public DepartmentDTO getDepartmentById(Long id) {
        log.info("Fetching department with ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Department with ID {} not found", id);
                    return new DepartmentNotFoundException("Department with ID " + id + " not found");
                });
        log.info("Department with ID {} fetched successfully", id);
        return departmentMapper.toDto(department);
    }

    public List<DepartmentDTO> getAllDepartments() {
        log.info("Fetching all departments");
        List<DepartmentDTO> departments = departmentRepository.findAll()
                .stream()
                .map(departmentMapper::toDto)
                .collect(Collectors.toList());
        log.info("Fetched {} departments", departments.size());
        return departments;
    }

    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        log.info("Updating department with ID: {}", id);
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Department with ID {} not found", id);
                    return new DepartmentNotFoundException("Department with ID " + id + " not found");
                });

        existing.setName(departmentDTO.getName());
        existing.setDescription(departmentDTO.getDescription());

        Department updated = departmentRepository.save(existing);
        log.info("Department with ID {} updated successfully", id);
        return departmentMapper.toDto(updated);
    }

    public void deleteDepartment(Long id) {
        log.info("Deleting department with ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Department with ID {} not found", id);
                    return new DepartmentNotFoundException("Department with ID " + id + " not found");
                });
        departmentRepository.delete(department);
        log.info("Department with ID {} deleted successfully", id);
    }
}