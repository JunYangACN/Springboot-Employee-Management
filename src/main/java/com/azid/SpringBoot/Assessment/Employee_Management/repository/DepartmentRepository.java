package com.azid.SpringBoot.Assessment.Employee_Management.repository;

import com.azid.SpringBoot.Assessment.Employee_Management.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
    boolean existsByName(String name);
}
