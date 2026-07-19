package com.marwaneqada.orm.repository;

import com.marwaneqada.orm.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findBySick(boolean sick);
    List<Patient> findByFullNameContainingIgnoreCase(String keyword);
    List<Patient> findByScoreGreaterThan(int minimumScore);
}
