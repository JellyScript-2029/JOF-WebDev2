package com.example.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pet_owner")
public class PetOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "owner", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Pet> pets = new ArrayList<>();

    public PetOwner() {
    }

    public PetOwner(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public void addPet(Pet pet) {
        pets.add(pet);
        pet.setOwner(this);
    }

    public void removePet(Pet pet) {
        pets.remove(pet);
        pet.setOwner(null);
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Pet> getPets() {
        return pets;
    }
}