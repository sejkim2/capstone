package com.example.app.personInoutStats;

import com.example.app.cctv.Cctv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PersonInoutStatsRepository extends JpaRepository<PersonInoutStats, Long> {

    // 특정 CCTV에 대한 통계 전체 조회
    List<PersonInoutStats> findByCctv(Cctv cctv);

    // 시간대 범위로 조회
    List<PersonInoutStats> findByCctvAndStartTimeBetween(Cctv cctv, LocalDateTime start, LocalDateTime end);
}
