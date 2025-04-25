package com.example.app.personRecognition;

import com.example.app.cctv.Cctv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PersonRecognitionRepository extends JpaRepository<PersonRecognition, Long> {

    // 특정 CCTV에서 인식된 사람 목록 조회
    List<PersonRecognition> findByCctv(Cctv cctv);

    // 인식 시각 기준으로 조회 (예: 시간대별 통계)
    List<PersonRecognition> findByRecognizedAtBetween(LocalDateTime start, LocalDateTime end);
}
