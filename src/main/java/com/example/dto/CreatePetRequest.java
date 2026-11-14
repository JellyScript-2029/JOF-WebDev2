package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePetRequest {

    @NotBlank(message = "Microchip number is required")
    @Size(min = 4, max = 30, message = "Microchip number must be between 4 and 30 characters")
    private String microchipNumber;

    @NotBlank(message = "Pet name is required")
    @Size(min = 1, max = 50, message = "Pet name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Species is required")
    @Size(max = 30, message = "Species must not exceed 30 characters")
    private String species;

    @Size(max = 50, message = "Breed must not exceed 50 characters")
    private String breed;

    @Min(value = 1, message = "Owner ID must be at least 1")
    private Long ownerId;

    @Email(message = "Contact email must be a valid email address")
    private String contactEmail;

    public CreatePetRequest() {
    }

    public String getMicrochipNumber() {
        return microchipNumber;
    }

    public void setMicrochipNumber(String microchipNumber) {
        this.microchipNumber = microchipNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}