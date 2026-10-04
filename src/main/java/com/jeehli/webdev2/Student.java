package com.jeehli.webdev2;
 
import jakarta.validation.constraints.*;
 
public class Student {
    private Long id;
 
    @NotBlank(message = "This is a required field")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
 
    @NotBlank(message = "You must put an email")
    @Email(message = "Provide a valid email address")
    private String email;
 
    private String course;
 
    @Min(value = 0, message = "Age cannot be less than 0")
    private int age;
 
    public Student() {
    }
 
    public Student(String name, String course, Long id, int age, String email) {
        this.name = name;
        this.course = course;
        this.id = id;
        this.age = age;
        this.email = email;
    }
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public String getCourse() {
        return course;
    }
 
    public void setCourse(String course) {
        this.course = course;
    }
 
    public int getAge() {
        return age;
    }
 
    public void setAge(int age) {
        this.age = age;
    }
}
 