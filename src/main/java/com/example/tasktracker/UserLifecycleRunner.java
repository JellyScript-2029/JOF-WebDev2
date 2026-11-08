package com.example.tasktracker;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// @Component makes Spring create this class automatically.
// CommandLineRunner makes its run() method execute once, right after the app starts.
@Component
public class UserLifecycleRunner implements CommandLineRunner {

    // Spring gives us the EntityManager, the main JPA tool for saving/finding/detaching entities
    @PersistenceContext
    private EntityManager entityManager;

    // persist() needs an active transaction, so we wrap the method in @Transactional.
    // When the method finishes, the transaction commits and Hibernate flushes (sends) the SQL.
    @Override
    @Transactional
    public void run(String... args) {

        // STEP 1: TRANSIENT
        // The object was just created with "new". JPA doesn't know about it yet, and there is no row in the database.
        User user = new User();
        user.setUsername("jhelianne");
        user.setEmail("original@email.com");
        user.setCreatedAt(LocalDateTime.now());
        System.out.println("1. TRANSIENT: user created, id = " + user.getId()); // id is null

        // STEP 2: MANAGED
        // persist() hands the object to JPA. It is now tracked by the persistence context.
        // With IDENTITY, the INSERT runs immediately, so the id is already filled in.
        entityManager.persist(user);
        System.out.println("2. MANAGED: user persisted, id = " + user.getId());
        System.out.println("   contains(user)? " + entityManager.contains(user)); // true

        // STEP 3: DETACHED
        // detach() removes the object from the persistence context.
        // The row already exists in the database, but JPA no longer watches this object.
        entityManager.detach(user);
        System.out.println("3. DETACHED: contains(user)? " + entityManager.contains(user)); // false

        // STEP 4: MODIFY THE DETACHED ENTITY
        // We change the email on the Java object only.
        // Because it is detached, JPA does not notice (no dirty checking).
        user.setEmail("changed@email.com");
        System.out.println("4. Email changed in Java to: " + user.getEmail());

        // STEP 5: DO NOT MERGE
        // We intentionally do NOT call entityManager.merge(user).
        // Without merge(), the change never reaches the database.
        System.out.println("5. No merge() called, so the database still has the ORIGINAL email.");

        // When this method ends, the transaction commits.
        // The database row keeps "original@email.com", not "changed@email.com".
    }
}