package com.example.app.visit;

import com.example.app.cctv.Cctv;
import com.example.app.cctv.CctvRepository;
import com.example.app.vehicle.Vehicle;
import com.example.app.vehicle.VehicleRepository;
import com.example.app.visit.dto.VisitRequestDto;
import com.example.app.visit.dto.VisitResponseDto;
import com.example.app.visit.dto.VisitRevisitResponseDto;
import com.example.app.visit.dto.VisitVehicleDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VisitService {

        private final VisitRepository visitRepository;
        private final VehicleRepository vehicleRepository;
        private final CctvRepository cctvRepository;

        @Transactional
        public void processVisit(VisitRequestDto dto) {
                var cctv = cctvRepository.findById(dto.getCctvId())
                                .orElseThrow(() -> new IllegalArgumentException("CCTV not found"));

                var vehicle = vehicleRepository.findByPlateNumber(dto.getVehicleNumber())
                                .orElseGet(() -> vehicleRepository.save(
                                                Vehicle.builder()
                                                                .plateNumber(dto.getVehicleNumber())
                                                                .build()));

                if (dto.getDirection().equalsIgnoreCase("in")) {
                        // 차량 입장: Visit 새로 생성 (exitTime 없음)
                        Visit visit = Visit.builder()
                                        .cctv(cctv)
                                        .vehicle(vehicle)
                                        .entryTime(dto.getTimestamp())
                                        .build();
                        visitRepository.save(visit);

                } else if (dto.getDirection().equalsIgnoreCase("out")) {
                        // 차량 출차: 가장 최근 Visit 찾아 exitTime 설정
                        Visit latestVisit = visitRepository.findTopByVehicleOrderByEntryTimeDesc(vehicle)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "No entry record found for vehicle"));

                        if (latestVisit.getExitTime() == null) {
                                latestVisit.setExitTime(dto.getTimestamp());
                                visitRepository.save(latestVisit);
                        }
                        // else: 이미 출차 완료된 경우는 무시 (또는 나중에 로직 추가할 수 있음)
                } else {
                        throw new IllegalArgumentException("Invalid direction value: must be 'in' or 'out'");
                }
        }

        public List<VisitResponseDto> getVisits(Long cctvId, LocalDateTime start, LocalDateTime end) {
                List<VisitVehicleDto> dtos = visitRepository.findVisitDtosByCctvAndTimeRange(cctvId, start, end);

                return dtos.stream()
                                .map(dto -> VisitResponseDto.builder()
                                                .vehicleId(dto.getPlateNumber())
                                                .in(dto.getEntryTime())
                                                .out(dto.getExitTime())
                                                .durationMinutes(Duration.between(
                                                                dto.getEntryTime(),
                                                                dto.getExitTime() != null ? dto.getExitTime()
                                                                                : LocalDateTime.now())
                                                                .toMinutes())
                                                .build())
                                .collect(Collectors.toList());
        }

        public Map<String, List<VisitRevisitResponseDto>> getRevisitData(Long cctvId, LocalDateTime start,
                        LocalDateTime end) {
                List<VisitVehicleDto> dtos = visitRepository.findVisitDtosByCctvAndTimeRange(cctvId, start, end);

                Map<String, List<VisitRevisitResponseDto>> result = new HashMap<>();
                for (VisitVehicleDto dto : dtos) {
                        result.computeIfAbsent(dto.getPlateNumber(), k -> new ArrayList<>())
                                        .add(VisitRevisitResponseDto.builder()
                                                        .in(dto.getEntryTime())
                                                        .out(dto.getExitTime())
                                                        .build());
                }
                return result;
        }
}
