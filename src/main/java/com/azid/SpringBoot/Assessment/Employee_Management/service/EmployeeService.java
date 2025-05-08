package com.azid.springboot.assessment.employee_management.service;

import com.azid.springboot.assessment.employee_management.dto.EmployeeDTO;
import com.azid.springboot.assessment.employee_management.entity.Department;
import com.azid.springboot.assessment.employee_management.entity.Employee;
import com.azid.springboot.assessment.employee_management.exception.*;
import com.azid.springboot.assessment.employee_management.mapper.EmployeeMapper;
import com.azid.springboot.assessment.employee_management.repository.DepartmentRepository;
import com.azid.springboot.assessment.employee_management.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.employeeMapper = employeeMapper;
    }

    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO) {
        try {
            log.info("Adding employee: {}", employeeDTO.getName());

            // Duplication check
            if (employeeRepository.existsByName(employeeDTO.getName()))
                throw new EmployeeAlreadyExistsException("Employee name already exists");

            if (employeeRepository.existsByEmail(employeeDTO.getEmail()))
                throw new EmailAlreadyExistsException("Email already exists");

            if (employeeRepository.existsByPhoneNo(employeeDTO.getPhoneNo()))
                throw new PhoneNoAlreadyExistsException("Phone number already exists");

            // Check department by name
            Department department = departmentRepository.findByName(employeeDTO.getDepartment().getName())
                    .orElseThrow(() -> new DepartmentNotFoundException("Department '" + employeeDTO.getDepartment().getName() + "' not found"));

            // Map DTO to Entity
            Employee employee = employeeMapper.toEntity(employeeDTO);
            employee.setDepartment(department);

            Employee saved = employeeRepository.save(employee);

            // Hide sensitive fields (ID)
            return employeeMapper.toDto(saved);


        } catch (DataAccessException e) {
            log.error("Database error while adding employee: {}", e.getMessage());
            throw new ServiceException("Database error occurred while adding employee", e);
        }
    }

    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        try {
            log.info("Updating employee ID: {}", id);

            Employee existing = employeeRepository.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + id + " not found"));

            // Duplication check (excluding self)
            if (employeeRepository.existsByNameAndIdNot(employeeDTO.getName(), id))
                throw new EmployeeAlreadyExistsException("Employee name already exists");

            if (employeeRepository.existsByEmailAndIdNot(employeeDTO.getEmail(), id))
                throw new EmailAlreadyExistsException("Email already exists");

            if (employeeRepository.existsByPhoneNoAndIdNot(employeeDTO.getPhoneNo(), id))
                throw new PhoneNoAlreadyExistsException("Phone number already exists");

            // Update employee fields
            existing.setName(employeeDTO.getName());
            existing.setEmail(employeeDTO.getEmail());
            existing.setPhoneNo(employeeDTO.getPhoneNo());

            // Department change
            if (employeeDTO.getDepartment().getName() != null) {
                Department department = departmentRepository.findByName(employeeDTO.getDepartment().getName())
                        .orElseThrow(() -> new DepartmentNotFoundException("Department '" + employeeDTO.getDepartment().getName() + "' not found"));
                existing.setDepartment(department);
            }

            Employee updated = employeeRepository.save(existing);
            return employeeMapper.toDto(updated);
        } catch (DataAccessException e) {
            throw new ServiceException("Database error occurred while updating employee", e);
        }
    }

    public EmployeeDTO getEmployeeById(Long id) {
        try {
            log.info("Fetching employee with ID: {}", id);
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + id + " not found"));
            return employeeMapper.toDto(employee);

        } catch (DataAccessException e) {
            log.error("Database error while fetching employee: {}", e.getMessage());
            throw new ServiceException("Database error occurred while fetching employee", e);
        }
    }

    public List<EmployeeDTO> getAllEmployees() {
        try {
            return employeeRepository.findAll().stream()
                    .map(employeeMapper::toDto)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new ServiceException("Database error occurred while fetching employees", e);
        }
    }

    public void deleteEmployee(Long id) {
        try {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + id + " not found"));
            employeeRepository.delete(employee);
        } catch (DataAccessException e) {
            throw new ServiceException("Database error occurred while deleting employee", e);
        }
    }
}