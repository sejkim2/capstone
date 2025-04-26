package com.example.app.personInoutStats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PersonInoutStatsRepository extends JpaRepository<PersonInoutStats, Long> {
    List<PersonInoutStats> findByCctv_CctvIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
            Long cctvId, LocalDateTime startTime, LocalDateTime endTime);
}
