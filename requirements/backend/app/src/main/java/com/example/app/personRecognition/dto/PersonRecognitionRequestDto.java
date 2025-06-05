package com.example.app.personRecognition.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonRecognitionRequestDto {
    private Long cctvId;
    private LocalDateTime timestamp;
    private String direction;
    private String gender;
    private String ageGroup;
}
