package com.example.app.personRecognition;

import com.example.app.personRecognition.dto.PersonRecognitionStatisticsResponseDto;
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
@RequestMapping("/api/person/statistics")
@RequiredArgsConstructor
public class PersonRecognitionController {

    private final PersonRecognitionService personRecognitionService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPersonStatistics(
            @RequestParam Long cctvId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime
    ) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        List<PersonRecognitionStatisticsResponseDto> statistics = personRecognitionService.getStatistics(cctvId, startDateTime, endDateTime);

        Map<String, Object> response = new HashMap<>();
        response.put("statistics", statistics);
        response.put("status", 200);
        response.put("message", "성별/연령 통계 데이터를 성공적으로 반환했습니다.");

        return ResponseEntity.ok(response);
    }
}
