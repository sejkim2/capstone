package com.example.app.controller;

import com.example.app.cctv.CctvRepository;
import com.example.app.favoriteVideo.FavoriteVideoRepository;
import com.example.app.personRecognition.PersonRecognitionRepository;
import com.example.app.user.UserRepository;
import com.example.app.vehicle.VehicleRepository;
import com.example.app.video.VideoRepository;
import com.example.app.visit.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cleanup")
@RequiredArgsConstructor
public class DataCleanupController {

    private final PersonRecognitionRepository personRecognitionRepository;
    private final FavoriteVideoRepository favoriteVideoRepository;
    private final VisitRepository visitRepository;
    private final VideoRepository videoRepository;
    private final VehicleRepository vehicleRepository;
    private final CctvRepository cctvRepository;
    private final UserRepository userRepository;

    @GetMapping
    @DeleteMapping
    @Transactional
    public ResponseEntity<String> deleteAllData() {

        System.out.println(">>> 서버 종료 중: 테스트 데이터 삭제 중...");
        personRecognitionRepository.deleteAll();
        favoriteVideoRepository.deleteAll();
        visitRepository.deleteAll();
        videoRepository.deleteAll();
        vehicleRepository.deleteAll();
        cctvRepository.deleteAll();
        userRepository.deleteAll();
        System.out.println(">>> 테스트 데이터 삭제 완료");

        return ResponseEntity.ok("모든 데이터가 삭제되었습니다.");
    }
}
