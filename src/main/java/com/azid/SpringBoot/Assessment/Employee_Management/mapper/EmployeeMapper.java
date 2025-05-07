package com.azid.springboot.assessment.employee_management.mapper;

import com.azid.springboot.assessment.employee_management.dto.DepartmentDTO;
import com.azid.springboot.assessment.employee_management.dto.EmployeeDTO;
import com.azid.springboot.assessment.employee_management.entity.Department;
import com.azid.springboot.assessment.employee_management.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    Employee toEntity(EmployeeDTO dto);

    EmployeeDTO toDto(Employee entity);

    Department toDepartmentEntity(DepartmentDTO dto);

    DepartmentDTO toDepartmentDto(Department entity);
}
