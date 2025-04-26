package com.example.app.personRecognition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PersonRecognitionRepository extends JpaRepository<PersonRecognition, Long> {
    List<PersonRecognition> findByCctv_CctvIdAndRecognizedAtBetween(Long cctvId, LocalDateTime start, LocalDateTime end);
}
