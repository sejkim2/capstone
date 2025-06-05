package com.example.app.visit;

import com.example.app.cctv.Cctv;
import com.example.app.vehicle.Vehicle;
import com.example.app.visit.dto.VisitVehicleDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    // 특정 CCTV에 연결된 방문 기록
    List<Visit> findByCctv(Cctv cctv);

    // 특정 차량의 방문 기록
    List<Visit> findByVehicle(Vehicle vehicle);

    // 특정 시간 구간의 방문 기록
    List<Visit> findByEntryTimeBetween(LocalDateTime start, LocalDateTime end);

    // 특정 차량이 특정 CCTV에 방문한 기록
    List<Visit> findByCctvAndVehicle(Cctv cctv, Vehicle vehicle);

    Optional<Visit> findTopByVehicleOrderByEntryTimeDesc(Vehicle vehicle);

    // DTO Projection 방식 - N+1 문제 없음
    @Query("SELECT new com.example.app.visit.dto.VisitVehicleDto(" +
           "v.vehicle.plateNumber, v.entryTime, v.exitTime) " +
           "FROM Visit v " +
           "WHERE v.cctv.cctvId = :cctvId AND v.entryTime BETWEEN :start AND :end")
    List<VisitVehicleDto> findVisitDtosByCctvAndTimeRange(
            @Param("cctvId") Long cctvId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
