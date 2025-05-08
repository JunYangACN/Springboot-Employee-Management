package com.azid.SpringBoot.Assessment.Employee_Management.DepartmentUnitTest;

import com.azid.springboot.assessment.employee_management.dto.DepartmentDTO;
import com.azid.springboot.assessment.employee_management.dto.DepartmentResponseDTO;
import com.azid.springboot.assessment.employee_management.entity.Department;
import com.azid.springboot.assessment.employee_management.exception.DepartmentAlreadyExistsException;
import com.azid.springboot.assessment.employee_management.exception.DepartmentNotFoundException;
import com.azid.springboot.assessment.employee_management.exception.ServiceException;
import com.azid.springboot.assessment.employee_management.mapper.DepartmentMapper;
import com.azid.springboot.assessment.employee_management.repository.DepartmentRepository;
import com.azid.springboot.assessment.employee_management.service.DepartmentService;
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
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentService departmentService;

    private DepartmentDTO departmentDTO;
    private Department department;

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
    }

    @Test
    void addDepartment_Success() {
        when(departmentRepository.existsByName("IT")).thenReturn(false);
        when(departmentMapper.toEntity(departmentDTO)).thenReturn(department);
        when(departmentRepository.save(department)).thenReturn(department);
        when(departmentMapper.toDto(department)).thenReturn(departmentDTO);

        DepartmentDTO result = departmentService.addDepartment(departmentDTO);

        assertNotNull(result);
        assertEquals(departmentDTO.getName(), result.getName());
        verify(departmentRepository, times(1)).existsByName("IT");
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void addDepartment_AlreadyExists() {
        when(departmentRepository.existsByName("IT")).thenReturn(true);

        DepartmentAlreadyExistsException exception = assertThrows(
                DepartmentAlreadyExistsException.class,
                () -> departmentService.addDepartment(departmentDTO)
        );

        assertEquals("Department with name 'IT' already exists", exception.getMessage());
        verify(departmentRepository, times(1)).existsByName("IT");
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void addDepartment_DatabaseError() {
        when(departmentRepository.existsByName("IT")).thenReturn(false);
        when(departmentMapper.toEntity(departmentDTO)).thenReturn(department);
        when(departmentRepository.save(department)).thenThrow(new DataAccessException("Database error") {});

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> departmentService.addDepartment(departmentDTO)
        );

        assertEquals("Failed to add department due to database error", exception.getMessage());
        assertTrue(exception.getCause() instanceof DataAccessException);
    }

    @Test
    void getDepartmentById_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentMapper.toDto(department)).thenReturn(departmentDTO);

        DepartmentResponseDTO result = departmentService.getDepartmentById(1L);

        assertNotNull(result);
        assertEquals(departmentDTO.getName(), result.getName());
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    void getDepartmentById_NotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        DepartmentNotFoundException exception = assertThrows(
                DepartmentNotFoundException.class,
                () -> departmentService.getDepartmentById(1L)
        );

        assertEquals("Department with id 1 not found", exception.getMessage());
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    void getDepartmentById_DatabaseError() {
        when(departmentRepository.findById(1L)).thenThrow(new DataAccessException("Database error") {});

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> departmentService.getDepartmentById(1L)
        );

        assertEquals("Failed to retrieve department due to database error", exception.getMessage());
        assertTrue(exception.getCause() instanceof DataAccessException);
    }
}