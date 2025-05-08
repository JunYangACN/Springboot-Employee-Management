package com.azid.springboot.assessment.employee_management.mapper;

import com.azid.springboot.assessment.employee_management.dto.DepartmentDTO;
import com.azid.springboot.assessment.employee_management.dto.DepartmentResponseDTO;
import com.azid.springboot.assessment.employee_management.dto.UpdateDepartmentDTO;
import com.azid.springboot.assessment.employee_management.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    Department toEntity(DepartmentDTO dto);
    DepartmentDTO toDto(Department department);

    Department toUpEntity(UpdateDepartmentDTO dto);
    UpdateDepartmentDTO toUpDto(Department department);

    Department toResEntity(DepartmentResponseDTO dto);
    DepartmentResponseDTO toResDto(Department department);
}
