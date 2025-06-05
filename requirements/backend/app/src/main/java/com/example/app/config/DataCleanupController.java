package com.example.app.controller;

import com.example.app.cctv.CctvRepository;
import com.example.app.favoriteVideo.FavoriteVideoRepository;
import com.example.app.personRecognition.PersonRecognitionRepository;
import com.example.app.user.UserRepository;
import com.example.app.vehicle.VehicleRepository;
import com.example.app.video.VideoRepository;
import com.example.app.visit.VisitRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/cleanup")
@RequiredArgsConstructor
public class DataCleanupController {

    private final PersonRecognitionRepository personRecognitionRepository;
    private final FavoriteVideoRepository favoriteVideoRepository;
    private final VisitRepository visitRepository;
    private final VideoRepository videoRepository;
    private final VehicleRepository vehicleRepository;
    private final CctvRepository cctvRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager em;

    @GetMapping
    @DeleteMapping
    @Transactional
    public ResponseEntity<String> deleteAllData() {
        System.out.println(">>> 테스트 데이터 삭제 중...");
        // personRecognitionRepository.deleteAll();
        // favoriteVideoRepository.deleteAll();
        // visitRepository.deleteAll();
        videoRepository.deleteAll();
        // vehicleRepository.deleteAll();
        // cctvRepository.deleteAll();
        // userRepository.deleteAll();

        // AUTO_INCREMENT 초기화
        // em.createNativeQuery("ALTER TABLE person_recognition AUTO_INCREMENT = 1").executeUpdate();
        // em.createNativeQuery("ALTER TABLE favorite_video AUTO_INCREMENT = 1").executeUpdate();
        // em.createNativeQuery("ALTER TABLE visit AUTO_INCREMENT = 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE video AUTO_INCREMENT = 1").executeUpdate();
        // em.createNativeQuery("ALTER TABLE vehicle AUTO_INCREMENT = 1").executeUpdate();
        // em.createNativeQuery("ALTER TABLE cctv AUTO_INCREMENT = 1").executeUpdate();
        // em.createNativeQuery("ALTER TABLE users AUTO_INCREMENT = 1").executeUpdate();

        System.out.println(">>> 테스트 데이터 삭제 완료 및 AUTO_INCREMENT 초기화 완료");

        return ResponseEntity.ok("모든 데이터가 삭제되었고 AUTO_INCREMENT도 초기화되었습니다.");
    }
}
