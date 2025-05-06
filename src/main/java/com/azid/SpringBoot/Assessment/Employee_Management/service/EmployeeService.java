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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.employeeMapper = employeeMapper;
    }

    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO) {
        boolean exists = employeeRepository.existsByName(employeeDTO.getName());
        if (exists) {
            throw new EmployeeAlreadyExistsException("Employee with name '" + employeeDTO.getName() + "' already exists");
        }

        // Validate department existence
        Long deptId = employeeDTO.getDepartment().getId();
        Department department = departmentRepository.findById(deptId)
                .orElseThrow(() -> new DepartmentNotFoundException("Department with ID '" + deptId + "' not found"));

        // Map DTO to entity and assign the full Department object
        Employee employee = employeeMapper.toEntity(employeeDTO);
        employee.setDepartment(department);

        // Save and map back to enriched DTO
        Employee saved = employeeRepository.save(employee);

        // Enrich response with full Department info
        return employeeMapper.toDto(saved);
    }

    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with ID " + id + " not found"));
        return employeeMapper.toDto(employee);
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        // Check if employee exists
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with ID " + id + " not found"));

        // Update basic fields
        existing.setName(employeeDTO.getName());
        existing.setEmail(employeeDTO.getEmail());
        existing.setPhoneNo(employeeDTO.getPhoneNo());

        // If a new department ID is provided, fetch and assign it
        if (employeeDTO.getDepartment() != null && employeeDTO.getDepartment().getId() != null) {
            Long deptId = employeeDTO.getDepartment().getId();
            Department department = departmentRepository.findById(deptId)
                    .orElseThrow(() -> new DepartmentNotFoundException("Department with ID " + deptId + " not found"));
            existing.setDepartment(department);
        }

        // Save and return updated DTO
        Employee updated = employeeRepository.save(existing);
        return employeeMapper.toDto(updated);
    }

    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with ID " + id + " not found"));
        employeeRepository.delete(employee);
    }
}
