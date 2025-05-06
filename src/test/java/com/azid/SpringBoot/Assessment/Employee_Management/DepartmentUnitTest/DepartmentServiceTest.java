package com.azid.SpringBoot.Assessment.Employee_Management.DepartmentUnitTest;

import com.azid.SpringBoot.Assessment.Employee_Management.dto.DepartmentDTO;
import com.azid.SpringBoot.Assessment.Employee_Management.entity.Department;
import com.azid.SpringBoot.Assessment.Employee_Management.exception.DepartmentAlreadyExistsException;
import com.azid.SpringBoot.Assessment.Employee_Management.exception.DepartmentNotFoundException;
import com.azid.SpringBoot.Assessment.Employee_Management.mapper.DepartmentMapper;
import com.azid.SpringBoot.Assessment.Employee_Management.repository.DepartmentRepository;
import com.azid.SpringBoot.Assessment.Employee_Management.service.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class DepartmentServiceTest {
    private DepartmentRepository departmentRepository;
    private DepartmentMapper departmentMapper;
    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        departmentRepository = mock(DepartmentRepository.class);
        departmentMapper = mock(DepartmentMapper.class);
        departmentService = new DepartmentService(departmentRepository, departmentMapper);
    }

    @Test
    void addDepartment_shouldAddSuccessfully() {
        DepartmentDTO dto = new DepartmentDTO(null, "IT", "Tech Department");
        Department entity = new Department(null, "IT", "Tech Department");
        Department saved = new Department(1L, "IT", "Tech Department");
        DepartmentDTO expected = new DepartmentDTO(1L, "IT", "Tech Department");

        when(departmentRepository.existsByName("IT")).thenReturn(false);
        when(departmentMapper.toEntity(dto)).thenReturn(entity);
        when(departmentRepository.save(entity)).thenReturn(saved);
        when(departmentMapper.toDto(saved)).thenReturn(expected);

        DepartmentDTO result = departmentService.addDepartment(dto);
        assertEquals(expected, result);
    }

    @Test
    void addDepartment_shouldThrowIfExists() {
        DepartmentDTO dto = new DepartmentDTO(null, "Finance", "Finance Dept");
        when(departmentRepository.existsByName("Finance")).thenReturn(true);

        assertThrows(DepartmentAlreadyExistsException.class,
                () -> departmentService.addDepartment(dto));
    }

    @Test
    void getDepartmentById_shouldReturnDto() {
        Department dept = new Department(1L, "HR", "Human Resources");
        DepartmentDTO dto = new DepartmentDTO(1L, "HR", "Human Resources");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(departmentMapper.toDto(dept)).thenReturn(dto);

        DepartmentDTO result = departmentService.getDepartmentById(1L);
        assertEquals(dto, result);
    }

    @Test
    void getDepartmentById_shouldThrowIfNotFound() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.getDepartmentById(99L));
    }

    @Test
    void deleteDepartment_shouldDelete() {
        Department dept = new Department(1L, "R&D", "Research");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));

        departmentService.deleteDepartment(1L);
        verify(departmentRepository).delete(dept);
    }
}
