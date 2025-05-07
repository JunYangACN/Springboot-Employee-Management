package com.azid.springboot.assessment.employee_management.repository;


import com.azid.springboot.assessment.employee_management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    boolean existsByName(String name);
}
