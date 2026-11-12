package com.example.config;

import com.example.entity.*;
import com.example.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Week9DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public Week9DataSeeder(UserRepository userRepository, DepartmentRepository departmentRepository,
            StudentRepository studentRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        if (departmentRepository.count() > 0)
            return;

        // Task 1: 1:1
        User u = new User("alice");
        u.setProfile(new UserProfile("Alice's profile"));
        userRepository.save(u);

        // Task 1: 1:N (save Department -> Employees persisted via cascade)
        Department d = new Department("Engineering");
        d.addEmployee(new Employee("Ana"));
        d.addEmployee(new Employee("Ben"));
        departmentRepository.save(d);

        // Task 2 / 4 data
        studentRepository.save(new Student("Carla"));
        studentRepository.save(new Student("Dan"));
        courseRepository.save(new Course("CS101"));
        courseRepository.save(new Course("MATH101"));

        System.out.println("Week 9 seed done: users=" + userRepository.count()
                + ", departments=" + departmentRepository.count()
                + ", students=" + studentRepository.count()
                + ", courses=" + courseRepository.count());
    }
}
