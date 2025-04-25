package com.example.app.visit;

import com.example.app.visit.dto.VisitResponseDto;
import com.example.app.visit.dto.VisitRevisitResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getVisits(
            @RequestParam Long cctvId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime
    ) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        List<VisitResponseDto> visits = visitService.getVisits(cctvId, startDateTime, endDateTime);

        Map<String, Object> response = new HashMap<>();
        response.put("visits", visits);
        response.put("status", 200);
        response.put("message", "방문 기록을 성공적으로 반환했습니다.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/revisit")
    public ResponseEntity<Map<String, Object>> getRevisitData(
            @RequestParam Long cctvId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime
    ) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        Map<String, List<VisitRevisitResponseDto>> revisitData = visitService.getRevisitData(cctvId, startDateTime, endDateTime);

        Map<String, Object> response = new HashMap<>();
        response.put("revisit_data", revisitData);
        response.put("status", 200);
        response.put("message", "재방문 기록을 성공적으로 반환했습니다.");

        return ResponseEntity.ok(response);
    }
}
