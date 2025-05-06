package com.azid.SpringBoot.Assessment.Employee_Management.repository;


import com.azid.SpringBoot.Assessment.Employee_Management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    boolean existsByName(String name);
}
