package com.azid.springboot.assessment.employee_management.repository;

import com.azid.springboot.assessment.employee_management.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
    boolean existsByName(String name);
    Optional<Department> findByName(String name);
}
