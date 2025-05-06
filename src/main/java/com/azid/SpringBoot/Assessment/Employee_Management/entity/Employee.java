package com.azid.SpringBoot.Assessment.Employee_Management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long id;

    @Column(name= "employee_name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name="phoneNo")
    private String phoneNo;

    @ManyToOne
    @JoinColumn(name ="department_id")
    private Department department;
}
