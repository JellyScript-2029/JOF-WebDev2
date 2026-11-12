package com.example.repository;

import com.example.entity.PetVaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PetVaccinationRepository extends JpaRepository<PetVaccination, Long> {
    List<PetVaccination> findByPetId(Long petId);
}