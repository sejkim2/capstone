package com.example.app.visit;

import com.example.app.cctv.Cctv;
import com.example.app.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
}
