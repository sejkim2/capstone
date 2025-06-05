package com.example.app.personRecognition;

import com.example.app.personRecognition.dto.PersonInoutRecordDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonRecognitionRepository extends JpaRepository<PersonRecognition, Long> {

    // 기존 Entity 반환 방식 (참고용, 불필요하면 제거 가능)
    List<PersonRecognition> findByCctv_CctvIdAndRecognizedAtBetween(Long cctvId, LocalDateTime start, LocalDateTime end);

    //DTO Projection 방식 - N+1 문제 없이 성능 최적화
    @Query("SELECT new com.example.app.personRecognition.dto.PersonInoutRecordDto(" +
           "c.cctvId, p.recognizedAt, p.direction, p.gender, p.ageGroup) " +
           "FROM PersonRecognition p JOIN p.cctv c " +
           "WHERE c.cctvId = :cctvId AND p.recognizedAt BETWEEN :start AND :end")
    List<PersonInoutRecordDto> findDtoByCctvIdAndRecognizedAtBetween(
            @Param("cctvId") Long cctvId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
