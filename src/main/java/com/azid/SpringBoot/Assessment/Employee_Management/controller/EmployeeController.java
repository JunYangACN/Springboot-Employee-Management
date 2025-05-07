package com.azid.springboot.assessment.employee_management.controller;

    import com.azid.springboot.assessment.employee_management.dto.EmployeeDTO;
    import com.azid.springboot.assessment.employee_management.service.EmployeeService;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import jakarta.validation.Valid;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.MethodArgumentNotValidException;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/employee")
    public class EmployeeController {
        private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);
        private final EmployeeService employeeService;

        public EmployeeController(EmployeeService employeeService) {
            this.employeeService = employeeService;
        }

        @PostMapping("/create-employee")
        public ResponseEntity<EmployeeDTO> addEmployee(@RequestBody @Valid EmployeeDTO employeeDTO) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String body = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(employeeDTO);

                log.info("Received request to add employee: {}", body);
                EmployeeDTO addedEmployee = employeeService.addEmployee(employeeDTO);
                log.info("Added Employee: {}", addedEmployee);
                return ResponseEntity.status(HttpStatus.CREATED).body(addedEmployee);
            } catch (Exception e) {
                log.error("Error adding employee: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        @GetMapping("/{id}")
        public ResponseEntity<?> getEmployee(@PathVariable Long id) {
            try {
                EmployeeDTO employee = employeeService.getEmployeeById(id);
                return ResponseEntity.ok(employee);
            } catch (IllegalArgumentException e) {
                log.error("Employee not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
            } catch (Exception e) {
                log.error("Error retrieving employee: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving employee");
            }
        }

        @GetMapping("/getAll")
        public ResponseEntity<?> getAllEmployee() {
            try {
                List<EmployeeDTO> employees = employeeService.getAllEmployees();
                return ResponseEntity.ok(employees);
            } catch (Exception e) {
                log.error("Error retrieving all employees: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving employees");
            }
        }

        @PutMapping("/update-employee/{id}")
        public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody @Valid EmployeeDTO employeeDTO) {
            try {
                EmployeeDTO updated = employeeService.updateEmployee(id, employeeDTO);
                return ResponseEntity.ok(updated);
            } catch (IllegalArgumentException e) {
                log.error("Employee not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
            } catch (Exception e) {
                log.error("Error updating employee: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating employee");
            }
        }

        @DeleteMapping("/delete-employee/{id}")
        public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
            try {
                employeeService.deleteEmployee(id);
                return ResponseEntity.ok("Employee ID: " + id + " deleted");
            } catch (IllegalArgumentException e) {
                log.error("Employee not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
            } catch (Exception e) {
                log.error("Error deleting employee: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting employee");
            }
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseBody
        public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
            String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
            log.error("Validation error: {}", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        @ExceptionHandler(Exception.class)
        @ResponseBody
        public ResponseEntity<String> handleGeneralExceptions(Exception ex) {
            log.error("An error occurred: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }