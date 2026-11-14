package com.example.service;

import com.example.dto.CreatePetRequest;
import com.example.dto.PetDTO;
import com.example.dto.UpdatePetRequest;
import com.example.entity.Pet;
import com.example.entity.PetOwner;
import com.example.exception.DuplicateMicrochipException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.PetOwnerRepository;
import com.example.repository.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService {

    private static final int MAX_PAGE_SIZE = 100;

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
                ownerId);
    }

    @Transactional(readOnly = true)
    public Page<PetDTO> findAll(int page, int size, String sortBy, String direction, String species) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), MAX_PAGE_SIZE);

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(sortDirection, sortBy));

        Page<Pet> petPage = (species != null && !species.isBlank())
                ? petRepository.findBySpeciesIgnoreCase(species.trim(), pageable)
                : petRepository.findAll(pageable);

        return petPage.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public PetDTO findById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
        return toDTO(pet);
    }

    @Transactional
    public PetDTO create(CreatePetRequest req) {
        if (petRepository.existsByMicrochipNumber(req.getMicrochipNumber())) {
            throw new DuplicateMicrochipException(
                    "Microchip number already registered: " + req.getMicrochipNumber());
        }

        Pet pet = new Pet(
                req.getMicrochipNumber(),
                req.getName(),
                req.getSpecies(),
                req.getBreed());

        if (req.getOwnerId() != null) {
            PetOwner owner = petOwnerRepository.findById(req.getOwnerId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("PetOwner not found with id: " + req.getOwnerId()));
            pet.setOwner(owner);
        }

        return toDTO(petRepository.save(pet));
    }

    @Transactional
    public PetDTO update(Long id, UpdatePetRequest req) {
        Pet existing = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        if (req.getMicrochipNumber() != null
                && !req.getMicrochipNumber().equals(existing.getMicrochipNumber())
                && petRepository.existsByMicrochipNumber(req.getMicrochipNumber())) {
            throw new DuplicateMicrochipException(
                    "Microchip number already registered: " + req.getMicrochipNumber());
        }

        existing.setMicrochipNumber(req.getMicrochipNumber());
        existing.setName(req.getName());
        existing.setSpecies(req.getSpecies());
        existing.setBreed(req.getBreed());

        if (req.getOwnerId() != null) {
            PetOwner owner = petOwnerRepository.findById(req.getOwnerId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("PetOwner not found with id: " + req.getOwnerId()));
            existing.setOwner(owner);
        } else {
            existing.setOwner(null);
        }

        return toDTO(petRepository.save(existing));
    }

    @Transactional
    public PetDTO patch(Long id, UpdatePetRequest req) {
        Pet existing = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

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
                    .orElseThrow(
                            () -> new ResourceNotFoundException("PetOwner not found with id: " + req.getOwnerId()));
            existing.setOwner(owner);
        }

        return toDTO(petRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pet not found with id: " + id);
        }
        petRepository.deleteById(id);
    }
}