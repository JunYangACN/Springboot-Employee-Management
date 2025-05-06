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
        boolean exists = employeeRepository.existsByName(employeeDTO.getEmployeeName());
        if (exists) {
            throw new EmployeeAlreadyExistsException("Employee with name '" + employeeDTO.getEmployeeName() + "' already exists");
        }

        Employee employee = employeeMapper.toEntity(employeeDTO);
        Employee saved = employeeRepository.save(employee);
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
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with ID " + id + " not found"));

        // Update fields
        existing.setName(employeeDTO.getEmployeeName());
        existing.setEmail(employeeDTO.getEmail());
        existing.setPhoneNo(employeeDTO.getPhoneNo());

        if (employeeDTO.getDepartment() != null) {
            Department department = departmentRepository.findById(employeeDTO.getDepartment().getId())
                    .orElseThrow(() -> new DepartmentNotFoundException("Department with ID" +employeeDTO.getDepartment().getId() + "not found"));
            existing.setDepartment(department);
        }

        Employee updated = employeeRepository.save(existing);
        return employeeMapper.toDto(updated);
    }

    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with ID " + id + " not found"));
        employeeRepository.delete(employee);
    }
}
