package com.jeehli.webdev2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

@Repository
public class StudentRepository {
    private final List<Student> studentList = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(100000);

    public StudentRepository() {
        studentList.add(new Student("Steve", "Computer Science", 72631L, 19, "steve@school.edu"));
        studentList.add(new Student("Vincent", "Nursing", 34684L, 21, "vincent@school.edu"));
        studentList.add(new Student("Kate", "Computer Engineering", 92856L, 18, "kate@school.edu"));
        studentList.add(new Student("Bernice", "Tourism", 23461L, 22, "bernice@school.edu"));
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public Student getStudentById(Long id) {
        return studentList.stream()
                .filter(s -> Objects.equals(s.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public void saveStudent(Student student) {
        student.setId(nextId.incrementAndGet());
        studentList.add(student);
    }

    public void deleteStudent(Long id) {
        studentList.removeIf(s -> Objects.equals(s.getId(), id));
    }

    public void updateStudentDetails(Long id, Student updatedStudent) {
        Student existing = getStudentById(id);
        if (existing != null) {
            existing.setName(updatedStudent.getName());
            existing.setAge(updatedStudent.getAge());
            existing.setEmail(updatedStudent.getEmail());
            existing.setCourse(updatedStudent.getCourse());
        }
    }
}
