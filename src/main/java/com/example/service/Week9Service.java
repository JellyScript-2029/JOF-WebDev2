package com.example.service;

import com.example.entity.*;
import com.example.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@Service
public class Week9Service {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public Week9Service(UserRepository userRepository, DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository, StudentRepository studentRepository,
            CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    // ---------- Task 1 ----------
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listUsers() {
        List<Map<String, Object>> out = new ArrayList<>();
        for (User u : userRepository.findAll()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("profileBio", u.getProfile() == null ? null : u.getProfile().getBio());
            out.add(m);
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listDepartments() {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Department d : departmentRepository.findAll()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", d.getId());
            m.put("name", d.getName());
            List<Map<String, Object>> emps = new ArrayList<>();
            for (Employee e : d.getEmployees()) {
                emps.add(Map.of("id", e.getId(), "name", e.getName()));
            }
            m.put("employees", emps);
            out.add(m);
        }
        return out;
    }

    @Transactional
    public void addEmployee(Long deptId, String name) {
        Department d = departmentRepository.findById(deptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        d.addEmployee(new Employee(name));
        departmentRepository.save(d);
    }

    // ---------- Task 4 (orphanRemoval) ----------
    @Transactional
    public void removeEmployee(Long deptId, Long empId) {
        Department d = departmentRepository.findById(deptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        Employee e = d.getEmployees().stream()
                .filter(x -> x.getId().equals(empId)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not in department"));
        d.removeEmployee(e);
        departmentRepository.save(d);
    }

    // ---------- Task 2 ----------
    @Transactional
    public void enroll(Long studentId, Long courseId, String grade) {
        Student s = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        Course c = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        enrollmentRepository.save(new Enrollment(s, c, LocalDate.now(), grade));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listEnrollments() {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Enrollment e : enrollmentRepository.findAll()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", e.getId());
            m.put("student", e.getStudent().getName());
            m.put("course", e.getCourse().getTitle());
            m.put("grade", e.getGrade());
            out.add(m);
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listCourses() {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Course c : courseRepository.findAll()) {
            out.add(Map.of("id", c.getId(), "title", c.getTitle()));
        }
        return out;
    }

    // ---------- Task 4 (shared Course survives) ----------
    @Transactional
    public void deleteStudent(Long id) {
        enrollmentRepository.deleteByStudentId(id); // remove the link rows first
        studentRepository.deleteById(id); // no cascade REMOVE: Course rows are untouched
    }

    // ---------- Task 3 ----------
    // NO @Transactional: session closes after findById ->
    // LazyInitializationException
    public String departmentNameBroken(Long empId) {
        Employee e = employeeRepository.findById(empId).orElseThrow();
        return e.getDepartment().getName();
    }

    // Fix (a): transaction keeps the session open -> 2 SELECTs
    @Transactional(readOnly = true)
    public String departmentNameTransactional(Long empId) {
        Employee e = employeeRepository.findById(empId).orElseThrow();
        return e.getDepartment().getName();
    }

    // Fix (b): fetch join -> 1 SELECT with a join
    public String departmentNameFetchJoin(Long empId) {
        Employee e = employeeRepository.findWithDepartmentById(empId).orElseThrow();
        return e.getDepartment().getName();
    }
}
