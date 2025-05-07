package com.azid.springboot.assessment.employee_management.repository;

import com.azid.springboot.assessment.employee_management.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
    boolean existsByName(String name);
}
