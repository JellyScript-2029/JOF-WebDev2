package com.example.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pet_vaccination", uniqueConstraints = @UniqueConstraint(name = "uk_pet_vaccine", columnNames = {
        "pet_id", "vaccine_id" }))
public class PetVaccination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate administeredDate;

    private LocalDate nextDueDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vaccine_id", nullable = false)
    private Vaccine vaccine;

    public PetVaccination() {
    }

    public PetVaccination(Pet pet, Vaccine vaccine, LocalDate administeredDate, LocalDate nextDueDate) {
        this.pet = pet;
        this.vaccine = vaccine;
        this.administeredDate = administeredDate;
        this.nextDueDate = nextDueDate;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getAdministeredDate() {
        return administeredDate;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public Pet getPet() {
        return pet;
    }

    public Vaccine getVaccine() {
        return vaccine;
    }
}