package com.example.app.personInoutStats;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/person/inout")
@RequiredArgsConstructor
public class PersonInoutStatsController {

    private final PersonInoutStatsService personInoutStatsService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getInoutStatistics(
            @RequestParam Long cctvId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime
    ) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        int[] inoutCounts = personInoutStatsService.getInoutStatistics(cctvId, startDateTime, endDateTime);

        Map<String, Object> response = new HashMap<>();
        response.put("entry_count", inoutCounts[0]);
        response.put("exit_count", inoutCounts[1]);
        response.put("status", 200);
        response.put("message", "입출 통계를 성공적으로 반환했습니다.");

        return ResponseEntity.ok(response);
    }
}
