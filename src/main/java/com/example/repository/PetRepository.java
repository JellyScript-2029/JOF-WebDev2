package com.example.repository;

import com.example.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    // TASK 3 derived queries
    List<Pet> findBySpeciesIgnoreCase(String species);

    List<Pet> findByNameContainingIgnoreCase(String keyword);

    // TASK 3 custom JPQL
    @Query("SELECT DISTINCT p.species FROM Pet p")
    List<String> findAllSpecies();
}
