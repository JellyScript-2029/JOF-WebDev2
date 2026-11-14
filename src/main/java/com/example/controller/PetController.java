package com.example.controller;

import com.example.dto.CreatePetRequest;
import com.example.dto.PetDTO;
import com.example.dto.UpdatePetRequest;
import com.example.service.PetService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public Page<PetDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String species) {

        String sortField = sort;
        String sortDir = direction;
        if (sort.contains(",")) {
            String[] parts = sort.split(",", 2);
            sortField = parts[0];
            sortDir = parts[1];
        }
        return petService.findAll(page, size, sortField, sortDir, species);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PetDTO> create(@Valid @RequestBody CreatePetRequest request) {
        PetDTO created = petService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDTO> update(@PathVariable Long id, @Valid @RequestBody UpdatePetRequest request) {
        return ResponseEntity.ok(petService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PetDTO> patch(@PathVariable Long id, @Valid @RequestBody UpdatePetRequest request) {
        return ResponseEntity.ok(petService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }
}