package com.example.config;

import com.example.entity.*;
import com.example.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Component
public class CapstoneSmokeTestRunner implements CommandLineRunner {

    private final PetOwnerRepository ownerRepository;
    private final VaccineRepository vaccineRepository;
    private final PetVaccinationRepository vaccinationRepository;

    public CapstoneSmokeTestRunner(PetOwnerRepository ownerRepository,
            VaccineRepository vaccineRepository,
            PetVaccinationRepository vaccinationRepository) {
        this.ownerRepository = ownerRepository;
        this.vaccineRepository = vaccineRepository;
        this.vaccinationRepository = vaccinationRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (ownerRepository.count() > 0)
            return;

        // 1. Save PetOwner + Pet (1:N via cascade)
        PetOwner owner = new PetOwner("Jhelianne Figuerres", "jhelianne@example.com");
        Pet pet = new Pet("MC-2001", "Mochi", "Dog", "Shih Tzu");
        owner.addPet(pet);
        ownerRepository.save(owner);

        // 2. Save Vaccine + PetVaccination (N:M association entity)
        Vaccine rabies = vaccineRepository.save(new Vaccine("Anti-Rabies", "Zoetis"));
        PetVaccination record = new PetVaccination(
                pet, rabies, LocalDate.now(), LocalDate.now().plusYears(1));
        vaccinationRepository.save(record);

        // 3. Read back graph and print verification
        PetOwner savedOwner = ownerRepository.findById(owner.getId()).orElseThrow();
        PetVaccination savedRecord = vaccinationRepository.findByPetId(pet.getId()).get(0);

        System.out.println("Capstone smoke test passed: owner=" + savedOwner.getFullName()
                + ", pets=" + savedOwner.getPets().size()
                + ", petName=" + savedRecord.getPet().getName()
                + ", vaccine=" + savedRecord.getVaccine().getName()
                + ", nextDue=" + savedRecord.getNextDueDate());
    }
}