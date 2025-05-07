package com.azid.springboot.assessment.employee_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {
    private Long id;

    @NotBlank(message = "Employee Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(
            regexp = "^\\+?\\d{1,3}?[- .]?\\(?(\\d{1,4})\\)?[- .]?\\d{1,4}[- .]?\\d{1,9}$",
            message = "Invalid phone number format"
    )
    private String phoneNo;

    private DepartmentDTO department;
}
