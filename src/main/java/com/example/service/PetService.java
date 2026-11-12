package com.example.service;

import com.example.entity.Pet;
import com.example.repository.PetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    // TASK 2
    public Pet registerPet(Pet p) {
        if (p.getMicrochipNumber() == null || p.getMicrochipNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "microchipNumber is required");
        }
        p.setId(null);
        return petRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pet not found: " + id));
    }

    @Transactional
    public Pet updatePet(Long id, Pet updated) {
        Pet existing = getPetById(id);
        existing.setMicrochipNumber(updated.getMicrochipNumber());
        existing.setName(updated.getName());
        existing.setSpecies(updated.getSpecies());
        existing.setBreed(updated.getBreed());
        return petRepository.save(existing);
    }

    @Transactional
    public void removePet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found: " + id);
        }
        petRepository.deleteById(id);
    }

    // TASK 3
    @Transactional(readOnly = true)
    public List<Pet> getBySpecies(String species) {
        return petRepository.findBySpeciesIgnoreCase(species);
    }

    @Transactional(readOnly = true)
    public List<Pet> searchByName(String keyword) {
        return petRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional(readOnly = true)
    public List<String> getAllSpecies() {
        return petRepository.findAllSpecies();
    }

    // TASK 4
    @Transactional
    public void batchRegister(List<Pet> pets) {
        for (Pet p : pets) {
            petRepository.save(p);
        }
    }
}
