package com.example.controller;

import com.example.service.Week9Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/week9")
public class Week9Controller {

    private final Week9Service service;

    public Week9Controller(Week9Service service) {
        this.service = service;
    }

    // Task 1
    @GetMapping("/users")
    public List<Map<String, Object>> users() {
        return service.listUsers();
    }

    @GetMapping("/departments")
    public List<Map<String, Object>> departments() {
        return service.listDepartments();
    }

    @PostMapping("/departments/{id}/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public void addEmployee(@PathVariable Long id, @RequestParam String name) {
        service.addEmployee(id, name);
    }

    // Task 4
    @DeleteMapping("/departments/{deptId}/employees/{empId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeEmployee(@PathVariable Long deptId, @PathVariable Long empId) {
        service.removeEmployee(deptId, empId);
    }

    @DeleteMapping("/students/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        service.deleteStudent(id);
    }

    // Task 2
    @PostMapping("/enrollments")
    @ResponseStatus(HttpStatus.CREATED)
    public void enroll(@RequestParam Long studentId, @RequestParam Long courseId,
            @RequestParam(required = false) String grade) {
        service.enroll(studentId, courseId, grade);
    }

    @GetMapping("/enrollments")
    public List<Map<String, Object>> enrollments() {
        return service.listEnrollments();
    }

    @GetMapping("/courses")
    public List<Map<String, Object>> courses() {
        return service.listCourses();
    }

    // Task 3: mode = broken | tx | fetch
    @GetMapping("/employees/{id}/department-name")
    public Map<String, String> departmentName(@PathVariable Long id, @RequestParam String mode) {
        String name = switch (mode) {
            case "broken" -> service.departmentNameBroken(id);
            case "tx" -> service.departmentNameTransactional(id);
            case "fetch" -> service.departmentNameFetchJoin(id);
            default -> throw new IllegalArgumentException("mode must be broken, tx or fetch");
        };
        return Map.of("department", name);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Constraint violation (e.g. duplicate enrollment)"));
    }
}
