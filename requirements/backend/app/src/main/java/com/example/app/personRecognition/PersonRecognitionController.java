package com.example.app.personRecognition;

import com.example.app.cctv.Cctv;
import com.example.app.cctv.CctvRepository;
import com.example.app.personRecognition.dto.PersonRecognitionRequestDto;
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
@RequestMapping("/api/person")
@RequiredArgsConstructor
public class PersonRecognitionController {

    private final PersonRecognitionService personRecognitionService;
    private final CctvRepository cctvRepository;

    @PostMapping("/recognition")
    public ResponseEntity<String> receivePersonRecognition(@RequestBody PersonRecognitionRequestDto request) {

        // 1. cctvId로 CCTV 찾기
        Cctv cctv = cctvRepository.findById(request.getCctvId())
                .orElseThrow(() -> new IllegalArgumentException("해당 CCTV를 찾을 수 없습니다."));

        // 2. 서비스로 넘겨 저장
        personRecognitionService.saveRecognition(
                cctv,
                LocalDateTime.parse(request.getTimestamp()),
                request.getDirection(),
                request.getGender(),
                request.getAgeGroup()
        );

        return ResponseEntity.ok("사람 인식 데이터 저장 성공");
    }

    // 성별/연령 통계 조회
    @GetMapping("/statistics")
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

    // 입출 통계 조회
    @GetMapping("/inout")
    public ResponseEntity<Map<String, Object>> getInoutStatistics(
            @RequestParam Long cctvId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime
    ) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        Map<String, Integer> counts = personRecognitionService.getInoutStatistics(cctvId, startDateTime, endDateTime);

        Map<String, Object> response = new HashMap<>();
        response.put("entry_count", counts.get("entry_count"));
        response.put("exit_count", counts.get("exit_count"));
        response.put("status", 200);
        response.put("message", "입출 통계를 성공적으로 반환했습니다.");

        return ResponseEntity.ok(response);
    }
}
