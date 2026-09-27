package com.jeehli;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Roster {
    private final List<Student> students;

    public Roster() {
        this.students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<String> generateAdultReport() {
        long count = students.stream()
                .filter(s -> s.getAge() >= 18)
                .count();

        List<String> names = students.stream()
                .filter(s -> s.getAge() >= 18)
                .map(Student::getName)
                .collect(Collectors.toList());

        System.out.println("Qualified students (18 or older): " + count);
        return names;
    }
}