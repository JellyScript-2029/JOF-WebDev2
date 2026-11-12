package com.example.repository;

import com.example.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    void deleteByStudentId(Long studentId);
}
