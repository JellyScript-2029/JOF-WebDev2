package com.example.config;

import com.example.entity.Pet;
import com.example.repository.PetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PetRepository petRepository;

    public DataSeeder(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public void run(String... args) {
        if (petRepository.count() == 0) {
            petRepository.save(new Pet("MC-0001", "Buddy", "Dog", "Labrador"));
            petRepository.save(new Pet("MC-0002", "Whiskers", "Cat", "Siamese"));
            petRepository.save(new Pet("MC-0003", "Bruno", "Dog", "Aspin"));
        }
        System.out.println("Seeded pets: " + petRepository.findAll().size());
    }
}
