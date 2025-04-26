package com.example.app.personRecognition;

import com.example.app.personRecognition.dto.PersonRecognitionStatisticsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonRecognitionService {

    private final PersonRecognitionRepository personRecognitionRepository;

    public List<PersonRecognitionStatisticsResponseDto> getStatistics(Long cctvId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<PersonRecognition> recognitions = personRecognitionRepository.findByCctv_CctvIdAndRecognizedAtBetween(cctvId, startDateTime, endDateTime);

        return recognitions.stream()
                .collect(Collectors.groupingBy(r -> r.getGender() + "_" + r.getAgeGroup(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("_");
                    return new PersonRecognitionStatisticsResponseDto(parts[0], parts[1], entry.getValue());
                })
                .collect(Collectors.toList());
    }
}
