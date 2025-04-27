package com.example.app.personRecognition.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PersonRecognitionStatisticsResponseDto {
    private String gender;
    private String ageGroup;
    private long count;
}
