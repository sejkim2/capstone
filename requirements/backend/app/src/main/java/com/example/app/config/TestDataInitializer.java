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
import java.time.LocalTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
public class TestDataInitializer {

    private final String[] GENDERS = {"male", "female"};
    private final String[] AGE_GROUPS = {"teen", "adult", "senior"};
    private final String[] DIRECTIONS = {"in", "out"};
    private final String[] LOCATIONS = {"zoneA", "zoneB", "zoneC", "mall", "parking lot", "gate", "entrance"};
    private final String[] CCTV_PREFIXES = {"CAM", "CCTV", "REC", "MONITOR"};

    private final Random random = new Random();

    private String randomFrom(String[] arr) {
        return arr[random.nextInt(arr.length)];
    }

    private String randomPlateNumber() {
        return "AB" + random.nextInt(1000) + (char)(random.nextInt(26) + 'A') + (char)(random.nextInt(26) + 'A');
    }

    private String randomCctvName(int userIndex) {
        return randomFrom(CCTV_PREFIXES) + "_" + UUID.randomUUID().toString().substring(0, 5) + "_user" + userIndex;
    }

    private LocalDateTime randomDateTimeBetween(LocalDate startDate, LocalDate endDate) {
        long startEpoch = startDate.atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC);
        long endEpoch = endDate.atTime(LocalTime.MAX).toEpochSecond(java.time.ZoneOffset.UTC);
        long randomEpoch = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch);
        return LocalDateTime.ofEpochSecond(randomEpoch, 0, java.time.ZoneOffset.UTC);
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
            LocalDate startDate = LocalDate.of(2025, 3, 1);
            LocalDate endDate = LocalDate.of(2025, 5, 1);

            for (int i = 1; i <= 3; i++) {
                User user = userRepository.save(User.builder()
                        .email("user" + i + "@test.com")
                        .password("1234")
                        .name("user" + i)
                        .build());

                for (int j = 1; j <= 2; j++) {
                    Cctv cctv = cctvRepository.save(Cctv.builder()
                            .user(user)
                            .name(randomCctvName(i))
                            .location(randomFrom(LOCATIONS))
                            .streamingUrl("http://stream.example.com/" + UUID.randomUUID())
                            .build());

                    for (int v = 0; v < 3; v++) {
                        LocalDateTime start = randomDateTimeBetween(startDate, endDate);
                        LocalDateTime end = start.plusMinutes(30);

                        Video video = videoRepository.save(Video.builder()
                                .cctv(cctv)
                                .s3Path("s3://videos/" + UUID.randomUUID() + ".mp4")
                                .startTime(start)
                                .endTime(end)
                                .build());

                        if (v == 0) {
                            favoriteVideoRepository.save(FavoriteVideo.builder()
                                    .user(user)
                                    .video(video)
                                    .build());
                        }
                    }

                    for (int k = 0; k < 5; k++) {
                        Vehicle vehicle = vehicleRepository.save(Vehicle.builder()
                                .plateNumber(randomPlateNumber())
                                .build());

                        LocalDateTime entry = randomDateTimeBetween(startDate, endDate);
                        LocalDateTime exit = entry.plusMinutes(20);

                        visitRepository.save(Visit.builder()
                                .cctv(cctv)
                                .vehicle(vehicle)
                                .entryTime(entry)
                                .exitTime(exit)
                                .build());
                    }

                    for (int r = 0; r < 5; r++) {
                        personRecognitionRepository.save(PersonRecognition.builder()
                                .cctv(cctv)
                                .recognizedAt(randomDateTimeBetween(startDate, endDate))
                                .direction(randomFrom(DIRECTIONS))
                                .gender(randomFrom(GENDERS))
                                .ageGroup(randomFrom(AGE_GROUPS))
                                .build());
                    }
                }
            }
        };
    }
}
