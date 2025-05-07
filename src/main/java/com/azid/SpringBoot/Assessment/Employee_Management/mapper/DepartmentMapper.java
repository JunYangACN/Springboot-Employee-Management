package com.azid.springboot.assessment.employee_management.mapper;

import com.azid.springboot.assessment.employee_management.dto.DepartmentDTO;
import com.azid.springboot.assessment.employee_management.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    Department toEntity(DepartmentDTO dto);
    DepartmentDTO toDto(Department department);
}
