package com.azid.springboot.assessment.employee_management.service;

import com.azid.springboot.assessment.employee_management.dto.DepartmentDTO;
import com.azid.springboot.assessment.employee_management.dto.DepartmentResponseDTO;
import com.azid.springboot.assessment.employee_management.dto.UpdateDepartmentDTO;
import com.azid.springboot.assessment.employee_management.entity.Department;
import com.azid.springboot.assessment.employee_management.exception.DepartmentAlreadyExistsException;
import com.azid.springboot.assessment.employee_management.exception.DepartmentInUseException;
import com.azid.springboot.assessment.employee_management.exception.DepartmentNotFoundException;
import com.azid.springboot.assessment.employee_management.exception.ServiceException;
import com.azid.springboot.assessment.employee_management.mapper.DepartmentMapper;
import com.azid.springboot.assessment.employee_management.repository.DepartmentRepository;
import com.azid.springboot.assessment.employee_management.repository.EmployeeRepository;
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
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
        this.employeeRepository = employeeRepository;
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

    public DepartmentResponseDTO getDepartmentById(Long id) {
        try {
            log.info("Fetching department with ID: {}", id);
            Department department = departmentRepository.findById(id)
                    .orElseThrow(() -> new DepartmentNotFoundException("Department with id " + id + " not found"));
            return departmentMapper.toResDto(department);
        } catch (DataAccessException e) {
            log.error("Database error while fetching department: {}", e.getMessage());
            throw new ServiceException("Failed to retrieve department due to database error", e);
        }
    }

    public List<DepartmentResponseDTO> getAllDepartments() {
        try {
            return departmentRepository.findAll().stream()
                    .map(departmentMapper::toResDto)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to retrieve departments due to database error", e);
        }
    }

    public UpdateDepartmentDTO updateDepartment(Long id, UpdateDepartmentDTO updatedepartmentDTO) {
        try {

            log.info("Updating department with ID: {}", id);
            log.info("New description: {}", updatedepartmentDTO.getDescription());

            Department existing = departmentRepository.findById(id)
                    .orElseThrow(() -> new DepartmentNotFoundException("Department with id " + id + " not found"));

            existing.setDescription(updatedepartmentDTO.getDescription());

            Department updated = departmentRepository.save(existing);
            log.info("Department with ID {} updated successfully", id);

            return departmentMapper.toUpDto(updated);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to update department due to database error", e);
        }
    }

    public void deleteDepartment(Long id) {
        try {
            Department department = departmentRepository.findById(id)
                    .orElseThrow(() -> new DepartmentNotFoundException("Department with id " + id + " not found"));
            // Check if any employee is assigned to this department

            boolean hasEmployees = employeeRepository.existsByDepartment(department);
            if (hasEmployees) {
                log.error("Cannot delete department with ID {} because it has assigned employees", id);
                throw new DepartmentInUseException("Department is assigned to existing employees.");
            }
            departmentRepository.delete(department);
            log.info("Department with ID {} deleted successfully", id);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to delete department due to database error", e);
        }
    }
}