package com.example.app.visit;

import com.example.app.cctv.Cctv;
import com.example.app.cctv.CctvRepository;
import com.example.app.visit.dto.VisitResponseDto;
import com.example.app.visit.dto.VisitRevisitResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final CctvRepository cctvRepository;

    public List<VisitResponseDto> getVisits(Long cctvId, LocalDateTime start, LocalDateTime end) {
        Cctv cctv = cctvRepository.findById(cctvId)
                .orElseThrow(() -> new IllegalArgumentException("해당 CCTV가 존재하지 않습니다."));

        List<Visit> visits = visitRepository.findByCctv(cctv).stream()
                .filter(v -> !v.getEntryTime().isBefore(start) && !v.getEntryTime().isAfter(end))
                .collect(Collectors.toList());

        return visits.stream()
                .map(visit -> VisitResponseDto.builder()
                        .vehicleId(visit.getVehicle().getPlateNumber())
                        .in(visit.getEntryTime())
                        .out(visit.getExitTime())
                        .durationMinutes(Duration.between(
                                visit.getEntryTime(),
                                visit.getExitTime() != null ? visit.getExitTime() : LocalDateTime.now()
                        ).toMinutes())
                        .build()
                ).collect(Collectors.toList());
    }

    public Map<String, List<VisitRevisitResponseDto>> getRevisitData(Long cctvId, LocalDateTime start, LocalDateTime end) {
        Cctv cctv = cctvRepository.findById(cctvId)
                .orElseThrow(() -> new IllegalArgumentException("해당 CCTV가 존재하지 않습니다."));

        List<Visit> visits = visitRepository.findByCctv(cctv).stream()
                .filter(v -> !v.getEntryTime().isBefore(start) && !v.getEntryTime().isAfter(end))
                .collect(Collectors.toList());

        Map<String, List<VisitRevisitResponseDto>> revisitData = new HashMap<>();

        for (Visit visit : visits) {
            String plateNumber = visit.getVehicle().getPlateNumber();
            revisitData.computeIfAbsent(plateNumber, k -> new ArrayList<>())
                       .add(VisitRevisitResponseDto.builder()
                               .in(visit.getEntryTime())
                               .out(visit.getExitTime())
                               .build());
        }

        return revisitData;
    }
}
