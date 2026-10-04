package com.jeehli.webdev2;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class StudentService {
    private StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student getStudentById(Long Id) {
        return studentRepository.getStudentById(Id);
    }

    public List<Student> getStudentList() {
        return studentRepository.getStudentList();
    }

    public void createStudent(Student student) {
        studentRepository.saveStudent(student);
    }

    public void updateStudent(Student student, Long Id) {
        studentRepository.updateStudentDetails(Id, student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteStudent(id);
    }

}