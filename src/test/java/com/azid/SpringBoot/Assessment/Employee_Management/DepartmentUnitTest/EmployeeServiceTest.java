package com.azid.SpringBoot.Assessment.Employee_Management.DepartmentUnitTest;

import com.azid.springboot.assessment.employee_management.dto.DepartmentDTO;
import com.azid.springboot.assessment.employee_management.dto.EmployeeDTO;
import com.azid.springboot.assessment.employee_management.entity.Department;
import com.azid.springboot.assessment.employee_management.entity.Employee;
import com.azid.springboot.assessment.employee_management.exception.DepartmentNotFoundException;
import com.azid.springboot.assessment.employee_management.exception.EmployeeNotFoundException;
import com.azid.springboot.assessment.employee_management.exception.ServiceException;
import com.azid.springboot.assessment.employee_management.mapper.EmployeeMapper;
import com.azid.springboot.assessment.employee_management.repository.DepartmentRepository;
import com.azid.springboot.assessment.employee_management.repository.EmployeeRepository;
import com.azid.springboot.assessment.employee_management.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeDTO employeeDTO;
    private Employee employee;
    private Department department;
    private DepartmentDTO departmentDTO;

    @BeforeEach
    void setUp() {
        departmentDTO = DepartmentDTO.builder()
                .id(1L)
                .name("IT")
                .description("Information Technology")
                .build();

        department = new Department();
        department.setId(1L);
        department.setName("IT");
        department.setDescription("Information Technology");

        employeeDTO = EmployeeDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNo("+1234567890")
                .department(departmentDTO)
                .build();

        employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setEmail("john.doe@example.com");
        employee.setPhoneNo("+1234567890");
        employee.setDepartment(department);
    }

    @Test
    void addEmployee_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeMapper.toEntity(employeeDTO)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.addEmployee(employeeDTO);

        assertNotNull(result);
        assertEquals(employeeDTO.getName(), result.getName());
        verify(departmentRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void addEmployee_DepartmentNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        DepartmentNotFoundException exception = assertThrows(
                DepartmentNotFoundException.class,
                () -> employeeService.addEmployee(employeeDTO)
        );

        assertEquals("Department with id 1 not found", exception.getMessage());
        verify(departmentRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void addEmployee_DatabaseError() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeMapper.toEntity(employeeDTO)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenThrow(new DataAccessException("Database error") {});

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> employeeService.addEmployee(employeeDTO)
        );

        assertEquals("Database error occurred while adding employee", exception.getMessage());
        assertTrue(exception.getCause() instanceof DataAccessException);
    }

    @Test
    void getEmployeeById_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals(employeeDTO.getName(), result.getName());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void getEmployeeById_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(1L)
        );

        assertEquals("Employee with id 1 not found", exception.getMessage());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void getEmployeeById_DatabaseError() {
        when(employeeRepository.findById(1L)).thenThrow(new DataAccessException("Database error") {});

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> employeeService.getEmployeeById(1L)
        );

        assertEquals("Database error occurred while fetching employee", exception.getMessage());
        assertTrue(exception.getCause() instanceof DataAccessException);
    }
}