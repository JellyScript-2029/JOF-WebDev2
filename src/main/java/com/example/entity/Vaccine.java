package com.example.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String manufacturer;

    @OneToMany(mappedBy = "vaccine", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<PetVaccination> vaccinations = new ArrayList<>();

    public Vaccine() {
    }

    public Vaccine(String name, String manufacturer) {
        this.name = name;
        this.manufacturer = manufacturer;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public List<PetVaccination> getVaccinations() {
        return vaccinations;
    }
}