package com.example.service;

import com.example.dto.CreatePetRequest;
import com.example.dto.PetDTO;
import com.example.dto.UpdatePetRequest;
import com.example.entity.Pet;
import com.example.entity.PetOwner;
import com.example.exception.DuplicateMicrochipException;
import com.example.repository.PetOwnerRepository;
import com.example.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final PetOwnerRepository petOwnerRepository;

    public PetService(PetRepository petRepository, PetOwnerRepository petOwnerRepository) {
        this.petRepository = petRepository;
        this.petOwnerRepository = petOwnerRepository;
    }

    public PetDTO toDTO(Pet pet) {
        Long ownerId = (pet.getOwner() != null) ? pet.getOwner().getId() : null;
        return new PetDTO(
                pet.getId(),
                pet.getMicrochipNumber(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                ownerId
        );
    }

    @Transactional(readOnly = true)
    public List<PetDTO> findAll() {
        return petRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<PetDTO> findById(Long id) {
        return petRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public PetDTO create(CreatePetRequest req) {
        if (req.getMicrochipNumber() == null || req.getMicrochipNumber().isBlank()) {
            throw new IllegalArgumentException("microchipNumber is required");
        }
        if (petRepository.existsByMicrochipNumber(req.getMicrochipNumber())) {
            throw new DuplicateMicrochipException(
                    "Microchip number already registered: " + req.getMicrochipNumber());
        }

        Pet pet = new Pet(
                req.getMicrochipNumber(),
                req.getName(),
                req.getSpecies(),
                req.getBreed()
        );

        if (req.getOwnerId() != null) {
            PetOwner owner = petOwnerRepository.findById(req.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner not found: " + req.getOwnerId()));
            pet.setOwner(owner);
        }

        Pet saved = petRepository.save(pet);
        return toDTO(saved);
    }

    @Transactional
    public Optional<PetDTO> update(Long id, UpdatePetRequest req) {
        return petRepository.findById(id).map(existing -> {
            if (req.getMicrochipNumber() != null
                    && !req.getMicrochipNumber().equals(existing.getMicrochipNumber())
                    && petRepository.existsByMicrochipNumber(req.getMicrochipNumber())) {
                throw new DuplicateMicrochipException(
                        "Microchip number already registered: " + req.getMicrochipNumber());
            }

            // Full replacement (PUT)
            existing.setMicrochipNumber(req.getMicrochipNumber());
            existing.setName(req.getName());
            existing.setSpecies(req.getSpecies());
            existing.setBreed(req.getBreed());

            if (req.getOwnerId() != null) {
                PetOwner owner = petOwnerRepository.findById(req.getOwnerId())
                        .orElseThrow(() -> new IllegalArgumentException("Owner not found: " + req.getOwnerId()));
                existing.setOwner(owner);
            } else {
                existing.setOwner(null);
            }

            return toDTO(petRepository.save(existing));
        });
    }

    @Transactional
    public Optional<PetDTO> patch(Long id, UpdatePetRequest req) {
        return petRepository.findById(id).map(existing -> {
            // Partial update (PATCH): overwrite only non-null fields
            if (req.getMicrochipNumber() != null) {
                if (!req.getMicrochipNumber().equals(existing.getMicrochipNumber())
                        && petRepository.existsByMicrochipNumber(req.getMicrochipNumber())) {
                    throw new DuplicateMicrochipException(
                            "Microchip number already registered: " + req.getMicrochipNumber());
                }
                existing.setMicrochipNumber(req.getMicrochipNumber());
            }
            if (req.getName() != null) {
                existing.setName(req.getName());
            }
            if (req.getSpecies() != null) {
                existing.setSpecies(req.getSpecies());
            }
            if (req.getBreed() != null) {
                existing.setBreed(req.getBreed());
            }
            if (req.getOwnerId() != null) {
                PetOwner owner = petOwnerRepository.findById(req.getOwnerId())
                        .orElseThrow(() -> new IllegalArgumentException("Owner not found: " + req.getOwnerId()));
                existing.setOwner(owner);
            }

            return toDTO(petRepository.save(existing));
        });
    }

    @Transactional
    public boolean delete(Long id) {
        if (!petRepository.existsById(id)) {
            return false;
        }
        petRepository.deleteById(id);
        return true;
    }
}