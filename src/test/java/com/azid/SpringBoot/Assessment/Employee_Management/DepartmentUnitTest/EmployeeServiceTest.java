package com.azid.SpringBoot.Assessment.Employee_Management.DepartmentUnitTest;

import com.azid.SpringBoot.Assessment.Employee_Management.dto.DepartmentDTO;
import com.azid.SpringBoot.Assessment.Employee_Management.dto.EmployeeDTO;
import com.azid.SpringBoot.Assessment.Employee_Management.entity.Department;
import com.azid.SpringBoot.Assessment.Employee_Management.entity.Employee;
import com.azid.SpringBoot.Assessment.Employee_Management.exception.DepartmentNotFoundException;
import com.azid.SpringBoot.Assessment.Employee_Management.exception.EmployeeNotFoundException;
import com.azid.SpringBoot.Assessment.Employee_Management.mapper.EmployeeMapper;
import com.azid.SpringBoot.Assessment.Employee_Management.repository.DepartmentRepository;
import com.azid.SpringBoot.Assessment.Employee_Management.repository.EmployeeRepository;
import com.azid.SpringBoot.Assessment.Employee_Management.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeServiceTest {
    private EmployeeRepository employeeRepository;
    private DepartmentRepository departmentRepository;
    private EmployeeMapper employeeMapper;
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        departmentRepository = mock(DepartmentRepository.class);
        employeeMapper = mock(EmployeeMapper.class);
        employeeService = new EmployeeService(employeeRepository, departmentRepository, employeeMapper);
    }

    @Test
    void createEmployee_shouldCreateSuccessfully() {
        Department dept = new Department(1L, "IT", "Tech");
        DepartmentDTO deptDTO = new DepartmentDTO(1L, "IT", "Tech");
        EmployeeDTO dto = new EmployeeDTO(null, "John Doe", "john@example.com", "+6012-345678", deptDTO);
        Employee emp = new Employee(null, "John Doe", "john@example.com", "+6012-345678", dept);
        Employee saved = new Employee(1L, "John Doe", "john@example.com", "+6012-345678", dept);
        EmployeeDTO expected = new EmployeeDTO(1L, "John Doe", "john@example.com", "+6012-345678", deptDTO);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(employeeMapper.toEntity(dto)).thenReturn(emp);
        when(employeeRepository.save(emp)).thenReturn(saved);
        when(employeeMapper.toDto(saved)).thenReturn(expected);

        EmployeeDTO result = employeeService.addEmployee(dto);
        assertEquals(expected, result);
    }

    @Test
    void createEmployee_shouldThrowIfDepartmentNotFound() {
        DepartmentDTO deptDTO = new DepartmentDTO(99L, "Unknown", "N/A");
        EmployeeDTO dto = new EmployeeDTO(null, "Alice", "alice@example.com", "+6012-999999", deptDTO);

        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> employeeService.addEmployee(dto));
    }

    @Test
    void getEmployeeById_shouldReturnDto() {
        Department dept = new Department(1L, "IT", "Tech");
        DepartmentDTO deptDTO = new DepartmentDTO(1L, "IT", "Tech");
        Employee emp = new Employee(1L, "John Doe", "john@example.com", "+6012-345678", dept);
        EmployeeDTO dto = new EmployeeDTO(1L, "John Doe", "john@example.com", "+6012-345678", deptDTO);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));
        when(employeeMapper.toDto(emp)).thenReturn(dto);

        EmployeeDTO result = employeeService.getEmployeeById(1L);
        assertEquals(dto, result);
    }

    @Test
    void getEmployeeById_shouldThrowIfNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(999L));
    }

    @Test
    void deleteEmployee_shouldDelete() {
        Department dept = new Department(1L, "IT", "Tech");
        Employee emp = new Employee(1L, "John Doe", "john@example.com", "+6012-345678", dept);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));
        employeeService.deleteEmployee(1L);
        verify(employeeRepository).delete(emp);
    }
}
