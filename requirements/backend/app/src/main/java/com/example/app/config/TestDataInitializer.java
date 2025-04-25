package com.example.app.config;

import com.example.app.cctv.Cctv;
import com.example.app.cctv.CctvRepository;
import com.example.app.personInoutStats.PersonInoutStats;
import com.example.app.personInoutStats.PersonInoutStatsRepository;
import com.example.app.personRecognition.PersonRecognition;
import com.example.app.personRecognition.PersonRecognitionRepository;
import com.example.app.user.User;
import com.example.app.user.UserRepository;
import com.example.app.vehicle.Vehicle;
import com.example.app.vehicle.VehicleRepository;
import com.example.app.video.Video;
import com.example.app.video.VideoRepository;
import com.example.app.visit.Visit;
import com.example.app.visit.VisitRepository;
import com.example.app.favoriteVideo.FavoriteVideo;
import com.example.app.favoriteVideo.FavoriteVideoRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class TestDataInitializer {

        @Bean
        @Transactional
        public CommandLineRunner initData(UserRepository userRepository,
                                          CctvRepository cctvRepository,
                                          VehicleRepository vehicleRepository,
                                          VisitRepository visitRepository,
                                          VideoRepository videoRepository,
                                          FavoriteVideoRepository favoriteVideoRepository,
                                          PersonRecognitionRepository personRecognitionRepository,
                                          PersonInoutStatsRepository personInoutStatsRepository) {
            return args -> {
        
                // 1. Users
                for (int i = 1; i <= 3; i++) {
                    User user = userRepository.save(User.builder()
                            .email("user" + i + "@test.com")
                            .password("1234")
                            .name("user" + i)
                            .build());
        
                    // 2. CCTV per user
                    for (int j = 1; j <= 2; j++) {
                        Cctv cctv = cctvRepository.save(Cctv.builder()
                                .user(user)
                                .name("CCTV" + j + "_user" + i)
                                .location("zone" + j)
                                .streamingUrl("http://stream.example.com/" + i + j)
                                .build());
        
                        // 3. Videos per CCTV
                        for (int v = 0; v < 3; v++) {
                            LocalDateTime start = LocalDateTime.of(2025, 4, 16, 9 + v, 0);
                            LocalDateTime end = start.plusMinutes(30);
                            Video video = videoRepository.save(Video.builder()
                                    .cctv(cctv)
                                    .s3Path("s3://videos/user" + i + "_cctv" + j + "_v" + v + ".mp4")
                                    .startTime(start)
                                    .endTime(end)
                                    .build());
        
                            // 4. Favorite only first video
                            if (v == 0) {
                                favoriteVideoRepository.save(FavoriteVideo.builder()
                                        .user(user)
                                        .video(video)
                                        .build());
                            }
                        }
        
                        // 5. Vehicles & visits
                        for (int k = 0; k < 5; k++) {
                            Vehicle vehicle = vehicleRepository.save(Vehicle.builder()
                                    .plateNumber("AB" + i + j + k + "CD")
                                    .build());
        
                            LocalDateTime entry = LocalDateTime.of(2025, 4, 16, 10 + k, 0);
                            LocalDateTime exit = entry.plusMinutes(20);
        
                            visitRepository.save(Visit.builder()
                                    .cctv(cctv)
                                    .vehicle(vehicle)
                                    .entryTime(entry)
                                    .exitTime(exit)
                                    .build());
                        }
        
                        // 6. PersonRecognition (English)
                        personRecognitionRepository.save(PersonRecognition.builder()
                                .cctv(cctv)
                                .recognizedAt(LocalDateTime.of(2025, 4, 16, 10, 10))
                                .gender("male")
                                .ageGroup("teen")
                                .build());
        
                        personRecognitionRepository.save(PersonRecognition.builder()
                                .cctv(cctv)
                                .recognizedAt(LocalDateTime.of(2025, 4, 16, 10, 20))
                                .gender("female")
                                .ageGroup("adult")
                                .build());
        
                        // 7. PersonInoutStats
                        personInoutStatsRepository.save(PersonInoutStats.builder()
                                .cctv(cctv)
                                .startTime(LocalDateTime.of(2025, 4, 16, 10, 0))
                                .endTime(LocalDateTime.of(2025, 4, 16, 11, 0))
                                .inCount(10 + j)
                                .outCount(5 + j)
                                .build());
                    }
                }
            };
        }        
} 