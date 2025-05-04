package com.example.app.config;

import com.example.app.cctv.CctvRepository;
import com.example.app.favoriteVideo.FavoriteVideoRepository;
import com.example.app.personRecognition.PersonRecognitionRepository;
import com.example.app.user.UserRepository;
import com.example.app.vehicle.VehicleRepository;
import com.example.app.video.VideoRepository;
import com.example.app.visit.VisitRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataCleaner {

    private final PersonRecognitionRepository personRecognitionRepository;
    private final FavoriteVideoRepository favoriteVideoRepository;
    private final VisitRepository visitRepository;
    private final VideoRepository videoRepository;
    private final VehicleRepository vehicleRepository;
    private final CctvRepository cctvRepository;
    private final UserRepository userRepository;

    @PreDestroy
    public void cleanupTestData() {
        System.out.println(">>> 서버 종료 중: 테스트 데이터 삭제 중...");

        personRecognitionRepository.deleteAll();
        favoriteVideoRepository.deleteAll();
        visitRepository.deleteAll();
        videoRepository.deleteAll();
        vehicleRepository.deleteAll();
        cctvRepository.deleteAll();
        userRepository.deleteAll();

        System.out.println(">>> 테스트 데이터 삭제 완료");
    }
}
