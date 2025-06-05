package com.example.app.config;

import com.example.app.cctv.Cctv;
import com.example.app.cctv.CctvRepository;
import com.example.app.user.User;
import com.example.app.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;
import java.util.UUID;

// @Configuration
public class CctvOnlyTestDataInitializer {

    private final String[] LOCATIONS = {
            "zoneA", "zoneB", "zoneC", "mall", "parking lot", "gate", "entrance"
    };

    private final String[] CCTV_PREFIXES = {
            "CAM", "CCTV", "REC", "MONITOR"
    };

    private final Random random = new Random();

    private String randomFrom(String[] arr) {
        return arr[random.nextInt(arr.length)];
    }

    private String randomCctvName() {
        return randomFrom(CCTV_PREFIXES) + "_" + UUID.randomUUID().toString().substring(0, 5);
    }

    @Bean
    @Transactional
    public CommandLineRunner initCctvOnly(UserRepository userRepository, CctvRepository cctvRepository) {
        return args -> {
            // 테스트용 유저 1명 생성
            User user = userRepository.save(User.builder()
                    .email("test_user@test.com")
                    .password("1234")
                    .name("test_user")
                    .build());

            // CCTV 4대 생성
            for (int i = 0; i < 4; i++) {
                cctvRepository.save(Cctv.builder()
                        .user(user)
                        .name(randomCctvName())
                        .location(randomFrom(LOCATIONS))
                        .streamingUrl("http://stream.example.com/" + UUID.randomUUID())
                        .build());
            }
        };
    }
}
