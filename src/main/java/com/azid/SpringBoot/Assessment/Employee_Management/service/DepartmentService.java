package com.azid.SpringBoot.Assessment.Employee_Management.service;

import com.azid.SpringBoot.Assessment.Employee_Management.dto.DepartmentDTO;
import com.azid.SpringBoot.Assessment.Employee_Management.entity.Department;
import com.azid.SpringBoot.Assessment.Employee_Management.exception.DepartmentAlreadyExistsException;
import com.azid.SpringBoot.Assessment.Employee_Management.exception.DepartmentNotFoundException;
import com.azid.SpringBoot.Assessment.Employee_Management.mapper.DepartmentMapper;
import com.azid.SpringBoot.Assessment.Employee_Management.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    public DepartmentDTO addDepartment(DepartmentDTO departmentDTO) {
        boolean exists = departmentRepository.existsByName(departmentDTO.getName());
        if (exists) {
            throw new DepartmentAlreadyExistsException("Department with name '" + departmentDTO.getName() + "' already exists");
        }

        Department department = departmentMapper.toEntity(departmentDTO);
        Department saved = departmentRepository.save(department);
        return departmentMapper.toDto(saved);
    }

    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department with ID " + id + " not found"));
        return departmentMapper.toDto(department);
    }

    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(departmentMapper::toDto)
                .collect(Collectors.toList());
    }

    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department with ID " + id + " not found"));

        // Update fields
        existing.setName(departmentDTO.getName());
        existing.setDescription(departmentDTO.getDescription());


        Department updated = departmentRepository.save(existing);
        return departmentMapper.toDto(updated);
    }

    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department with ID " + id + " not found"));
        departmentRepository.delete(department);
    }
}
