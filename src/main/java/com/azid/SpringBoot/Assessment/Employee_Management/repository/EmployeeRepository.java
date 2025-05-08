package com.azid.springboot.assessment.employee_management.repository;


import com.azid.springboot.assessment.employee_management.entity.Department;
import com.azid.springboot.assessment.employee_management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneNo(String phoneNo);
    boolean existsByPhoneNoAndIdNot(String phoneNo, Long id);
    boolean existsByDepartment(Department department);
}
