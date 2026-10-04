package com.jeehli.webdev2;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping()
    public String getAllStudents(Model model) {
        List<Student> students = studentService.getStudentList();
        model.addAttribute("students", students);
        return "students";
    }

    @PostMapping("/create")
    public String createStudent(@Valid @ModelAttribute("student") Student student, BindingResult result) {
        if (result.hasErrors()) {
            return "create-student-form";
        }
        studentService.createStudent(student);
        return "redirect:/students";
    }

    @PostMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        if (student != null) {
            studentService.deleteStudent(id);
        }
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String showEditStudentForm(@PathVariable("id") Long id, Model model) {
        Student student = studentService.getStudentById(id);

        if (student == null) {
            return "redirect:/students";
        }

        model.addAttribute("student", student);
        model.addAttribute("id", id);
        return "update-student-form";
    }

    @GetMapping("/create")
    public String showCreateStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "create-student-form";
    }

    @PostMapping("/edit/{id}")
    public String editStudent(@PathVariable("id") Long id,
            @Valid @ModelAttribute("student") Student updatedStudent,
            BindingResult result,
            Model model) {
        if (studentService.getStudentById(id) == null) {
            return "redirect:/students";
        }

        if (result.hasErrors()) {
            model.addAttribute("id", id);
            return "update-student-form";
        }

        studentService.updateStudent(updatedStudent, id);
        return "redirect:/students";
    }
}