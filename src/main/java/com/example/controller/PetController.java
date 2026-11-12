package com.example.controller;

import com.example.entity.Pet;
import com.example.service.PetService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    // TASK 2
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pet create(@RequestBody Pet pet) {
        return petService.registerPet(pet);
    }

    @GetMapping
    public List<Pet> getAll() {
        return petService.getAllPets();
    }

    @GetMapping("/{id}")
    public Pet getOne(@PathVariable Long id) {
        return petService.getPetById(id);
    }

    @PutMapping("/{id}")
    public Pet update(@PathVariable Long id, @RequestBody Pet pet) {
        return petService.updatePet(id, pet);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        petService.removePet(id);
    }

    // TASK 3
    @GetMapping("/species")
    public List<Pet> bySpecies(@RequestParam String name) {
        return petService.getBySpecies(name);
    }

    @GetMapping("/search")
    public List<Pet> search(@RequestParam String keyword) {
        return petService.searchByName(keyword);
    }

    @GetMapping("/species-list")
    public List<String> speciesList() {
        return petService.getAllSpecies();
    }

    // TASK 4
    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public void batch(@RequestBody List<Pet> pets) {
        petService.batchRegister(pets);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Constraint violation (e.g. duplicate microchipNumber)"));
    }
}
