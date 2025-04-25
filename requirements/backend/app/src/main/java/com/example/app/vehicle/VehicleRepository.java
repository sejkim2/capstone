package com.example.app.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // 차량 번호로 중복 확인 또는 조회
    boolean existsByPlateNumber(String plateNumber);

    Optional<Vehicle> findByPlateNumber(String plateNumber);
}
