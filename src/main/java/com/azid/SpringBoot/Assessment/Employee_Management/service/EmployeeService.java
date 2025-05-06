package com.azid.SpringBoot.Assessment.Employee_Management.service;

import com.azid.SpringBoot.Assessment.Employee_Management.dto.EmployeeDTO;
import com.azid.SpringBoot.Assessment.Employee_Management.entity.Department;
import com.azid.SpringBoot.Assessment.Employee_Management.entity.Employee;
import com.azid.SpringBoot.Assessment.Employee_Management.exception.DepartmentNotFoundException;
import com.azid.SpringBoot.Assessment.Employee_Management.exception.EmployeeAlreadyExistsException;
import com.azid.SpringBoot.Assessment.Employee_Management.exception.EmployeeNotFoundException;
import com.azid.SpringBoot.Assessment.Employee_Management.mapper.EmployeeMapper;
import com.azid.SpringBoot.Assessment.Employee_Management.repository.DepartmentRepository;
import com.azid.SpringBoot.Assessment.Employee_Management.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.employeeMapper = employeeMapper;
    }

    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO) {
        log.info("Adding employee: {}", employeeDTO.getEmployeeName());
        boolean exists = employeeRepository.existsByName(employeeDTO.getEmployeeName());
        if (exists) {
            log.warn("Employee with name '{}' already exists", employeeDTO.getEmployeeName());
            throw new EmployeeAlreadyExistsException("Employee with name '" + employeeDTO.getEmployeeName() + "' already exists");
        }

        Employee employee = employeeMapper.toEntity(employeeDTO);
        Employee saved = employeeRepository.save(employee);
        log.info("Employee '{}' added successfully", saved.getName());
        return employeeMapper.toDto(saved);
    }

    public EmployeeDTO getEmployeeById(Long id) {
        log.info("Fetching employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee with ID {} not found", id);
                    return new EmployeeNotFoundException("Employee with ID " + id + " not found");
                });
        log.info("Employee with ID {} fetched successfully", id);
        return employeeMapper.toDto(employee);
    }

    public List<EmployeeDTO> getAllEmployees() {
        log.info("Fetching all employees");
        List<EmployeeDTO> employees = employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
        log.info("Fetched {} employees", employees.size());
        return employees;
    }

    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        log.info("Updating employee with ID: {}", id);
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee with ID {} not found", id);
                    return new EmployeeNotFoundException("Employee with ID " + id + " not found");
                });

        existing.setName(employeeDTO.getEmployeeName());
        existing.setEmail(employeeDTO.getEmail());
        existing.setPhoneNo(employeeDTO.getPhoneNo());

        if (employeeDTO.getDepartment() != null) {
            Department department = departmentRepository.findById(employeeDTO.getDepartment().getId())
                    .orElseThrow(() -> {
                        log.error("Department with ID {} not found", employeeDTO.getDepartment().getId());
                        return new DepartmentNotFoundException("Department with ID " + employeeDTO.getDepartment().getId() + " not found");
                    });
            existing.setDepartment(department);
        }

        Employee updated = employeeRepository.save(existing);
        log.info("Employee with ID {} updated successfully", id);
        return employeeMapper.toDto(updated);
    }

    public void deleteEmployee(Long id) {
        log.info("Deleting employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee with ID {} not found", id);
                    return new EmployeeNotFoundException("Employee with ID " + id + " not found");
                });
        employeeRepository.delete(employee);
        log.info("Employee with ID {} deleted successfully", id);
    }
}