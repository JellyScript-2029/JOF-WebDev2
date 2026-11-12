package com.example.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // TASK 1 version: cascade = CascadeType.ALL
    // TASK 4 version (safe composition): cascade = {PERSIST, MERGE} + orphanRemoval = true
    @OneToMany(mappedBy = "department",
               cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    public Department() {}
    public Department(String name) { this.name = name; }

    public void addEmployee(Employee e) {
        employees.add(e);
        e.setDepartment(this);
    }

    public void removeEmployee(Employee e) {
        employees.remove(e);
        e.setDepartment(null);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Employee> getEmployees() { return employees; }

    @Override public String toString() { return "Department{id=" + id + ", name=" + name + "}"; }
}
