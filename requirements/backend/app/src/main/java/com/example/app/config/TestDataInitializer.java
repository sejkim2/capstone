package com.example.app.config;

import com.example.app.cctv.Cctv;
import com.example.app.cctv.CctvRepository;
import com.example.app.favoriteVideo.FavoriteVideo;
import com.example.app.favoriteVideo.FavoriteVideoRepository;
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
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
public class TestDataInitializer {

    private final String[] GENDERS = {"male", "female"};
    private final String[] AGE_GROUPS = {"teen", "adult"};
    private final String[] DIRECTIONS = {"in", "out"};
    private final String[] LOCATIONS = {"zoneA", "zoneB", "zoneC", "mall", "parking lot", "gate", "entrance"};
    private final String[] CCTV_PREFIXES = {"CAM", "CCTV", "REC", "MONITOR"};

    private final Random random = new Random();

    private String randomFrom(String[] arr) {
        return arr[random.nextInt(arr.length)];
    }

    private String randomPlateNumber() {
        return "AB" + random.nextInt(1000)
                + (char) (random.nextInt(26) + 'A')
                + (char) (random.nextInt(26) + 'A');
    }

    private String randomCctvName(int userIndex) {
        return randomFrom(CCTV_PREFIXES) + "_" + UUID.randomUUID().toString().substring(0, 5) + "_user" + userIndex;
    }

    @Bean
    @Transactional
    public CommandLineRunner initData(UserRepository userRepository,
                                      CctvRepository cctvRepository,
                                      VehicleRepository vehicleRepository,
                                      VisitRepository visitRepository,
                                      VideoRepository videoRepository,
                                      FavoriteVideoRepository favoriteVideoRepository,
                                      PersonRecognitionRepository personRecognitionRepository) {
        return args -> {
            LocalDate startDate = LocalDate.of(2025, 5, 8);
            LocalDate endDate = LocalDate.now();

            // 🚗 1000개의 차량을 미리 생성
            List<Vehicle> vehicles = new ArrayList<>();
            Set<String> plateNumbers = new HashSet<>();
            while (vehicles.size() < 10) {
                String plate = randomPlateNumber();
                if (plateNumbers.add(plate)) {
                    Vehicle vehicle = vehicleRepository.save(Vehicle.builder()
                            .plateNumber(plate)
                            .build());
                    vehicles.add(vehicle);
                }
            }

            for (int i = 1; i <= 2; i++) {
                User user = userRepository.save(User.builder()
                        .email("user" + i + "@test.com")
                        .password("1234")
                        .name("user" + i)
                        .build());

                for (int j = 0; j < 2; j++) {
                    Cctv cctv = cctvRepository.save(Cctv.builder()
                            .user(user)
                            .name(randomCctvName(i))
                            .location(randomFrom(LOCATIONS))
                            .streamingUrl("http://stream.example.com/" + UUID.randomUUID())
                            .build());

                    boolean favoriteSet = false;

                    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

                        // 🎥 Video 생성
                        LocalDateTime videoStart = date.atTime(random.nextInt(24), random.nextInt(60));
                        LocalDateTime videoEnd = videoStart.plusMinutes(30);
                        Video video = videoRepository.save(Video.builder()
                                .cctv(cctv)
                                .s3Path("s3://videos/" + UUID.randomUUID() + ".mp4")
                                .startTime(videoStart)
                                .endTime(videoEnd)
                                .build());

                        // ⭐ 첫 영상만 찜 처리
                        if (!favoriteSet) {
                            favoriteVideoRepository.save(FavoriteVideo.builder()
                                    .user(user)
                                    .video(video)
                                    .build());
                            favoriteSet = true;
                        }

                        // 🚗 Visit & Vehicle 재사용
                        for (int k = 0; k < 2; k++) {
                            Vehicle vehicle = vehicles.get(random.nextInt(vehicles.size()));

                            LocalDateTime entry = date.atTime(random.nextInt(24), random.nextInt(60));
                            LocalDateTime exit = entry.plusMinutes(20 + random.nextInt(30));

                            visitRepository.save(Visit.builder()
                                    .cctv(cctv)
                                    .vehicle(vehicle)
                                    .entryTime(entry)
                                    .exitTime(exit)
                                    .build());
                        }

                        // 🧍 PersonRecognition 생성 (100개)
                        for (int r = 0; r < 2; r++) {
                            LocalDateTime recognizedAt = date.atTime(random.nextInt(24), random.nextInt(60));

                            personRecognitionRepository.save(PersonRecognition.builder()
                                    .cctv(cctv)
                                    .recognizedAt(recognizedAt)
                                    .direction(randomFrom(DIRECTIONS))
                                    .gender(randomFrom(GENDERS))
                                    .ageGroup(randomFrom(AGE_GROUPS))
                                    .build());
                        }
                    }
                }
            }
        };
    }
}
