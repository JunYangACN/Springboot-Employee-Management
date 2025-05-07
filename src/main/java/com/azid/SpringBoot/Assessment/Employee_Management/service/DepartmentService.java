package com.azid.springboot.assessment.employee_management.service;

import com.azid.springboot.assessment.employee_management.dto.DepartmentDTO;
import com.azid.springboot.assessment.employee_management.entity.Department;
import com.azid.springboot.assessment.employee_management.exception.DepartmentAlreadyExistsException;
import com.azid.springboot.assessment.employee_management.exception.DepartmentNotFoundException;
import com.azid.springboot.assessment.employee_management.exception.ServiceException;
import com.azid.springboot.assessment.employee_management.mapper.DepartmentMapper;
import com.azid.springboot.assessment.employee_management.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
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
        try {
            log.info("Adding department: {}", departmentDTO.getName());
            if (departmentRepository.existsByName(departmentDTO.getName())) {
                throw new DepartmentAlreadyExistsException("Department with name '" + departmentDTO.getName() + "' already exists");
            }

            Department department = departmentMapper.toEntity(departmentDTO);
            Department savedDepartment = departmentRepository.save(department);
            log.info("Department '{}' added successfully", savedDepartment.getName());
            return departmentMapper.toDto(savedDepartment);
        } catch (DataAccessException e) {
            log.error("Database error while adding department: {}", e.getMessage());
            throw new ServiceException("Failed to add department due to database error", e);
        }
    }

    public DepartmentDTO getDepartmentById(Long id) {
        try {
            log.info("Fetching department with ID: {}", id);
            Department department = departmentRepository.findById(id)
                    .orElseThrow(() -> new DepartmentNotFoundException("Department with id " + id + " not found"));
            return departmentMapper.toDto(department);
        } catch (DataAccessException e) {
            log.error("Database error while fetching department: {}", e.getMessage());
            throw new ServiceException("Failed to retrieve department due to database error", e);
        }
    }

    public List<DepartmentDTO> getAllDepartments() {
        try {
            return departmentRepository.findAll().stream()
                    .map(departmentMapper::toDto)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to retrieve departments due to database error", e);
        }
    }

    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        try {
            Department existing = departmentRepository.findById(id)
                    .orElseThrow(() -> new DepartmentNotFoundException("Department with id " + id + " not found"));

            existing.setName(departmentDTO.getName());
            existing.setDescription(departmentDTO.getDescription());

            Department updated = departmentRepository.save(existing);
            return departmentMapper.toDto(updated);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to update department due to database error", e);
        }
    }

    public void deleteDepartment(Long id) {
        try {
            Department department = departmentRepository.findById(id)
                    .orElseThrow(() -> new DepartmentNotFoundException("Department with id " + id + " not found"));
            departmentRepository.delete(department);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to delete department due to database error", e);
        }
    }
}